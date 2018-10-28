package com.kamron.pogoiv.devMethods.gameMasterParser;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.Form;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.FormSettings;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.ItemTemplate;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.PogoJson;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.PokemonSettings;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ApplicationDatabaseUpdater {

    public static void main(String args[]) throws InvalidObjectException {

        List<ItemTemplate> items = getDataFromGamemasterJson();

        Pattern pokedexRegex = Pattern.compile(".*FORMS_V([0-9]{4})_.*");
        HashMap<Integer, FormSettings> formsByPokedex = new HashMap<>();
        ArrayList<FormSettings> pokemonWithMultipleForms = new ArrayList<>();
        HashMap<String, HashMap<String, PokemonSettings>> pokemonFormsByName = new HashMap<>();

        for (ItemTemplate item : items) {
            if (item.getPokemonSettings() != null) {
                PokemonSettings poke = item.getPokemonSettings();
                poke.setTemplateID(item.getTemplateId());
                HashMap<String, PokemonSettings> formHash;
                if (pokemonFormsByName.containsKey(poke.getPokemonId())) {
                    formHash = pokemonFormsByName.get(poke.getPokemonId());
                } else {
                    formHash = new HashMap<>();
                    pokemonFormsByName.put(poke.getPokemonId(), formHash);
                }
                formHash.put(poke.getForm(), poke);
            }
            if (item.getFormSettings() != null) {
                FormSettings form = item.getFormSettings();
                Matcher m = pokedexRegex.matcher(item.getTemplateId());
                if (m.matches()) {
                    formsByPokedex.put(Integer.parseInt(m.group(1)), form);
                    if (form.getForms() != null && form.getForms().size() > 1) {
                        // Multiple forms; handle with special care
                        pokemonWithMultipleForms.add(form);
                    }
                } else {
                    throw new InvalidObjectException(null);
                }
            }
        }

        printIntegersXml(formsByPokedex, pokemonWithMultipleForms, pokemonFormsByName);
        printFormsXml(pokemonWithMultipleForms, pokemonFormsByName);
        printTypeDifferencesSuggestions(pokemonWithMultipleForms, pokemonFormsByName, formsByPokedex);
    }

    private static void printTypeDifferencesSuggestions(ArrayList<FormSettings> pokemonWithMultipleForms, HashMap<String, HashMap<String, PokemonSettings>> pokemonFormsByName, HashMap<Integer, FormSettings> formsByPokedex) {
        System.out.println("Here's type difference suggestions to allow GoIV to differentiate between pokemon forms:\n");

        for (FormSettings formSetting : pokemonWithMultipleForms) {
            HashMap<String, PokemonSettings> formHash = pokemonFormsByName.get(formSetting.getPokemon());

            HashMap<String, Integer> typeCounter = new HashMap<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                formHash.forEach((s, pokemonSettings) ->
                        {
                            int count = typeCounter.containsKey(pokemonSettings.getType()) ? typeCounter.get(pokemonSettings.getType()) : 0;
                            typeCounter.put(pokemonSettings.getType(), count + 1);
                            int count2 = typeCounter.containsKey(pokemonSettings.getType2()) ? typeCounter.get(pokemonSettings.getType2()) : 0;
                            typeCounter.put(pokemonSettings.getType2(), count2 + 1);

                        }
                );
            }

            final boolean[] hasUnique = {false};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                typeCounter.forEach((s, integer) -> {
                    if (integer == 1) {
                        System.out.println(s + " is unique for " + formSetting.getPokemon() + getDexIDOf(formSetting.getPokemon(), formsByPokedex));
                        hasUnique[0] = true;
                    }
                });
            }
            if (hasUnique[0] == false) {
                System.out.println(formSetting.getPokemon() + " has no unique typing. :((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((");
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N) private static int getDexIDOf(String pokemon, HashMap<Integer, FormSettings> formsByPokedex) {

        final int[] returner = {-1};
        formsByPokedex.forEach((integer, formSettings) ->
        {
            if (formSettings.getPokemon().equals(pokemon)) {
                returner[0] = integer - 1;
            }
        });
        if (returner[0] == -1) {

            throw new Error("should be able to find pokemon");
        }
        return returner[0];
    }

    /**
     * Reads the GAME_MASTER.json located in the project root directory, and returns a List<ItemTemplate> containing all the data contained in the Json.
     *
     * @return
     */
    private static List<ItemTemplate> getDataFromGamemasterJson() {
        URL url = null;
        try {
            url = new URL("https://raw.githubusercontent.com/pokemongo-dev-contrib/pokemongo-game-master/master/versions/latest/GAME_MASTER.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        PogoJson data = new Gson().fromJson(reader, PogoJson.class);

        return data.getItemTemplates();
    }

    /**
     * Prints out the contents that should be in Integers.xml
     *
     * @param formsByPokedex
     * @param pokemonWithMultipleForms
     * @param pokemonFormsByName
     */
    private static void printIntegersXml(HashMap<Integer, FormSettings> formsByPokedex, ArrayList<FormSettings> pokemonWithMultipleForms, HashMap<String, HashMap<String, PokemonSettings>> pokemonFormsByName) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n");

        // Output attack value of default form for each pokedex entry (no forms considered)
        stringBuilder.append("<integer-array name=\"attack\">\n");
        int maxPokedex = Collections.max(formsByPokedex.keySet());
        for (int i = 0; i <= maxPokedex; i++) {
            if (formsByPokedex.containsKey(i)) {
                FormSettings form = formsByPokedex.get(i);
                HashMap<String, PokemonSettings> formHash = pokemonFormsByName.get(form.getPokemon());
                PokemonSettings poke = formHash.get(null);
                stringBuilder.append(" <item>" + poke.getStats().getBaseAttack() + "</item> <!--" + titleCase(form.getPokemon()) + "-->\n");
            }
        }
        stringBuilder.append(" </integer-array>\n");

        // Output defense value of default form for each pokedex entry (no forms considered)
        stringBuilder.append("<integer-array name=\"defense\">\n");
        for (int i = 0; i <= maxPokedex; i++) {
            if (formsByPokedex.containsKey(i)) {
                FormSettings form = formsByPokedex.get(i);
                HashMap<String, PokemonSettings> formHash = pokemonFormsByName.get(form.getPokemon());
                PokemonSettings poke = formHash.get(null);
                stringBuilder.append(" <item>" + poke.getStats().getBaseDefense() + "</item> <!--" + titleCase(form.getPokemon()) + "-->\n");
            }
        }
        stringBuilder.append(" </integer-array>\n");

        // Output stamina value of default form for each pokedex entry (no forms considered)
        stringBuilder.append("<integer-array name=\"stamina\">\n");
        for (int i = 0; i <= maxPokedex; i++) {
            if (formsByPokedex.containsKey(i)) {
                FormSettings form = formsByPokedex.get(i);
                HashMap<String, PokemonSettings> formHash = pokemonFormsByName.get(form.getPokemon());
                PokemonSettings poke = formHash.get(null);
                stringBuilder.append(" <item>" + poke.getStats().getBaseStamina() + "</item> <!--" + titleCase(form.getPokemon()) + "-->\n");
            }
        }
        stringBuilder.append(" </integer-array>\n");

        // Output parent evolution information for each pokedex entry (no forms considered)
        stringBuilder.append("<integer-array name=\"devolutionNumber\">\n");
        for (int i = 0; i <= maxPokedex; i++) {
            if (formsByPokedex.containsKey(i)) {
                FormSettings form = formsByPokedex.get(i);
                HashMap<String, PokemonSettings> formHash = pokemonFormsByName.get(form.getPokemon());
                PokemonSettings poke = formHash.get(null);
                int parentPokedex = -1;
                if (poke.getParentPokemonId() != null) {
                    for (Map.Entry<Integer, FormSettings> entry : formsByPokedex.entrySet()) {
                        if (entry.getValue().getPokemon().equals(poke.getParentPokemonId())) {
                            parentPokedex = entry.getKey();
                            parentPokedex -= 1; // Changing from pokedex number to array index; this may not work if there are gaps
                            break;
                        }
                    }
                }
                stringBuilder.append(" <item>" + parentPokedex + "</item> <!--" + titleCase(form.getPokemon()) + "-->\n");
            }
        }
        stringBuilder.append(" </integer-array>\n");

        // Output candy evolution cost of default form for each pokedex entry (no forms considered)
        stringBuilder.append("<integer-array name=\"evolutionCandyCost\">\n");
        for (int i = 0; i <= maxPokedex; i++) {
            if (formsByPokedex.containsKey(i)) {
                FormSettings form = formsByPokedex.get(i);

                HashMap<String, PokemonSettings> formHash = pokemonFormsByName.get(form.getPokemon());
                PokemonSettings poke = formHash.get(null);
                Integer evolveCandy = null;

                if (form.getPokemon().contains("TURTWIG")){
                    System.out.println();
                }
                //todo fix better - niantic changed how they store evolve candy cost from gen3+, you get it from evolutionbranch .getcandycost
                //This implementation assumes that all evolution branches have the same evolution cost.


                if (poke.getEvolutionBranch() != null) {
                    evolveCandy = poke.getEvolutionBranch().get(0).getCandyCost();
                }
                if (evolveCandy == null){
                    poke.getCandyToEvolve();
                }

                if (evolveCandy == null) {
                    evolveCandy = -1;
                }

                stringBuilder.append(" <item>" + evolveCandy + "</item> <!--" + titleCase(form.getPokemon()) + "-->\n");
            }
        }
        stringBuilder.append(" </integer-array>\n");


        // Output candy name for each pokedex entry (no forms considered)
        stringBuilder.append("<integer-array name=\"candyNames\">\n");
        for (int i = 0; i <= maxPokedex; i++) {
            if (formsByPokedex.containsKey(i)) {
                FormSettings form = formsByPokedex.get(i);
                HashMap<String, PokemonSettings> formHash = pokemonFormsByName.get(form.getPokemon());
                PokemonSettings poke = formHash.get(null);

                String familyName = poke.getFamilyId().replace("FAMILY_", "");
                int familyPokedex = -1;
                for (Map.Entry<Integer, FormSettings> entry : formsByPokedex.entrySet()) {
                    if (entry.getValue().getPokemon().equals(familyName)) {
                        familyPokedex = entry.getKey();
                        familyPokedex -= 1; // Changing from pokedex number to array index; this may not work if there are gaps
                        break;
                    }
                }
                stringBuilder.append(" <item>" + familyPokedex + "</item> <!--" + titleCase(form.getPokemon()) + "-->\n");
            }
        }
        stringBuilder.append(" </integer-array>\n");

        // Output index into forms data of each pokedex entry
        stringBuilder.append("<integer-array name=\"formsCountIndex\">\n");
        for (int i = 0; i <= maxPokedex; i++) {
            if (formsByPokedex.containsKey(i)) {
                FormSettings form = formsByPokedex.get(i);
                int multiIndex = pokemonWithMultipleForms.indexOf(form);
                stringBuilder.append(" <item>" + multiIndex + "</item> <!--" + titleCase(form.getPokemon()) + "-->\n");
            }
        }

        stringBuilder.append(" </integer-array>\n");
        stringBuilder.append("</resources>\n");

        //System.out.println(stringBuilder);
        writeFile("integers.xml", stringBuilder.toString());
    }

    /**
     * Prints out all the contents that should be in forms.xml
     *
     * @param pokemonWithMultipleForms
     * @param pokemonFormsByName
     */
    private static void printFormsXml(ArrayList<FormSettings> pokemonWithMultipleForms, HashMap<String, HashMap<String, PokemonSettings>> pokemonFormsByName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n");

        // Output the number of forms for each multi-form pokemon
        stringBuilder.append("<integer-array name=\"formsCount\">\n");
        for (FormSettings form : pokemonWithMultipleForms) {

            stringBuilder.append(" <item>" + form.getForms().size() + "</item> <!--" + titleCase(form.getPokemon()) + "-->\n");

        }
        stringBuilder.append(" </integer-array>\n");

        // Output the form name of each form for each multi-form pokemon
        stringBuilder.append("<string-array name=\"formNames\">\n");
        for (FormSettings form : pokemonWithMultipleForms) {
            stringBuilder.append("  <!--" + titleCase(form.getPokemon()) + "-->\n");
            for (Form subform : form.getForms()) {
                stringBuilder.append("  <item>" + formName(subform.getForm(), form.getPokemon()) + "</item>\n");


            }
        }

        stringBuilder.append(" </string-array>\n");

        // Output the attack value of each form for each multi-form pokemon
        stringBuilder.append("<integer-array name=\"formAttack\">\n");
        for (FormSettings form : pokemonWithMultipleForms) {

            stringBuilder.append("  <!--" + titleCase(form.getPokemon()) + "-->\n");
            HashMap<String, PokemonSettings> formHash = pokemonFormsByName.get(form.getPokemon());
            for (Form subform : form.getForms()) {
                PokemonSettings poke = formHash.get(subform.getForm());
                int value = -1; // Default value should be ignored by GoIV since it won't yield a match
                if (poke != null) {
                    value = poke.getStats().getBaseAttack();
                }
                stringBuilder.append("  <item>" + value + "</item> <!-- " + formName(subform.getForm(), form.getPokemon()) + " -->\n");
            }
        }

        stringBuilder.append(" </integer-array>\n");

        // Output the defense value of each form for each multi-form pokemon
        stringBuilder.append("<integer-array name=\"formDefense\">\n");
        for (FormSettings form : pokemonWithMultipleForms) {
            stringBuilder.append("  <!--" + titleCase(form.getPokemon()) + "-->\n");
            HashMap<String, PokemonSettings> formHash = pokemonFormsByName.get(form.getPokemon());
            for (Form subform : form.getForms()) {

                PokemonSettings poke = formHash.get(subform.getForm());
                int value = -1; // Default value should be ignored by GoIV since it won't yield a match
                if (poke != null) {
                    value = poke.getStats().getBaseDefense();
                }
                stringBuilder.append("  <item>" + value + "</item> <!-- " + formName(subform.getForm(), form.getPokemon()) + " -->\n");


            }
        }

        stringBuilder.append(" </integer-array>\n");

        // Output the attack value of each form for each multi-form pokemon
        stringBuilder.append("<integer-array name=\"formStamina\">\n");
        for (FormSettings form : pokemonWithMultipleForms) {
            stringBuilder.append("  <!--" + titleCase(form.getPokemon()) + "-->\n");
            HashMap<String, PokemonSettings> formHash = pokemonFormsByName.get(form.getPokemon());
            for (Form subform : form.getForms()) {
                PokemonSettings poke = formHash.get(subform.getForm());
                int value = -1; // Default value should be ignored by GoIV since it won't yield a match
                if (poke != null) {
                    value = poke.getStats().getBaseStamina();
                }
                stringBuilder.append("  <item>" + value + "</item> <!-- " + formName(subform.getForm(), form.getPokemon()) + " -->\n");
            }
        }

        stringBuilder.append(" </integer-array>\n");

        stringBuilder.append("</resources>\n");

        //System.out.println(stringBuilder);
        writeFile("forms.xml", stringBuilder.toString());
    }

    /**
     * Converts a string like 'RAICHU_ALOLAN' to 'Alolan Form', or 'ARCEUS_DARK' to 'Dark Form'.
     *
     * @param form
     * @param pokemon
     * @return
     */
    private static String formName(String form, String pokemon) {
        String simpleName = form.substring(pokemon.length() + 1); // Skip past pokemon name and underscore
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return titleCase(simpleName) + " Form";
        }
        throw new Error("Run the generator with a higher java version");
    }

    /**
     * Converts text such as "THIS IS annoyingly WEIRd CapitaLIZATION" to "This Is Annoyingly Weird Capitalization" - capitalizing each word.
     *
     * @param str
     * @return
     */
    private static String titleCase(String str) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Arrays.stream(str.toLowerCase().split("[ _]")).map(word -> Character.toTitleCase(word.charAt(0)) + word.substring(1)).collect(Collectors.joining(" "));
        }
        throw new Error("Run the generator with a higher java version");
    }

    private static void writeFile(String fileName, String content) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "utf-8"))) {
            writer.write(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
