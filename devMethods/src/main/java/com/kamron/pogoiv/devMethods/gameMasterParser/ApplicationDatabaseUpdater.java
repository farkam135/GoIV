package com.kamron.pogoiv.devMethods.gameMasterParser;

import com.google.gson.Gson;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.Form;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.FormSettings;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.Data;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.PogoJson;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.Pokemon;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.Stats;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.Template;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ApplicationDatabaseUpdater {

    private static final String integerArrayFormat = " <item>%d</item>";
    private static final String stringArrayFormat = " <item>%s</item>";
    private static final String commentFormat = " <!-- %s -->\n";

    public static void main(String[] args) {

        Map<Integer, List<String>> names = getNameMapFromCSV();
        List<Data> data = getDataFromGamemasterJson();

        HashMap<Integer, FormSettings> formsByPokedex = new HashMap<>(); // Stores form data index by NatDex number
        ArrayList<FormSettings> pokemonWithMultipleForms = new ArrayList<>(); // stores form data for pokemon with
        // multiple forms
        HashMap<String, HashMap<String, Pokemon>> pokemonFormsByName = new HashMap<>(); // stores pokemon data by
        // species and form id
        HashMap<String, Integer> dexNumberLookup = new HashMap<>(); // species name -> dex #
        Set<Integer> dexNosInSystem; // Dex numbers for pokemon actually in the app (could hve forms for unsupported
        // pokemon because of e.g. missing stat blocks)

        for (Data datum : data) {
            Pokemon poke = datum.getPokemon();
            if (poke != null) {
                Stats stats = poke.getStats();
                if (stats.getBaseAttack() == null || stats.getBaseDefense() == null || stats.getBaseStamina() == null) {
                    continue; // TODO: Handle pokemon with bad stats.
                }
                poke.setTemplateId(datum.getTemplateId()); // Inject template ID directly into pokemon object

                // Add this pokemon to the map for it's species, indexed by the form name
                pokemonFormsByName.putIfAbsent(poke.getUniqueId(), new HashMap<>());
                if (pokemonFormsByName.get(poke.getUniqueId()).putIfAbsent(poke.getForm(), poke) != null) {
                    System.out.println(String.format(
                            "WARNING: Found second \"%s\" form for %s (templateId on new form is %s)",
                            unnull(poke.getForm(), "null"), poke.getUniqueId(), poke.getTemplateId()
                    ));
                }
            }

            if (datum.getFormSettings() != null) {
                FormSettings form = new SpecificFormSettings(datum.getFormSettings());
                // Extract the NatDex number for the pokemon from the template ID
                int dexNumber = Integer.parseInt(datum.getTemplateId().substring(7, 11));
                formsByPokedex.put(dexNumber, form);
                dexNumberLookup.put(form.getName(), dexNumber);

                if (form.getForms() != null && form.getForms().size() > 1) {
                    // Multiple forms; handle with special care
                    pokemonWithMultipleForms.add(form);
                }
            }
        }

        dexNumberLookup.put(null, 0); // Sneaky addition to dodge an if in printIntegers

        dexNosInSystem = pokemonFormsByName.keySet().stream().map(dexNumberLookup::get).collect(Collectors.toSet());

        printIntegersXml(formsByPokedex, pokemonWithMultipleForms, pokemonFormsByName, dexNumberLookup);
        printFormsXml(pokemonWithMultipleForms, pokemonFormsByName);
        printTypeDifferencesSuggestions(pokemonWithMultipleForms, pokemonFormsByName, dexNumberLookup);
        printPokemonXml(names, dexNosInSystem);
    }

    private static void printTypeDifferencesSuggestions(ArrayList<FormSettings> pokemonWithMultipleForms,
                                                        HashMap<String, HashMap<String, Pokemon>> pokemonFormsByName,
                                                        HashMap<String, Integer> dexNumberLookup) {
        System.out.println("Here's type difference suggestions to allow GoIV to differentiate between forms:\n");

        for (FormSettings formSetting : pokemonWithMultipleForms) {
            String formName = formSetting.getName();
            HashMap<String, Pokemon> formHash = pokemonFormsByName.get(formName);

            HashMap<String, Integer> typeCounter = new HashMap<>();
            for (Pokemon pokemon : formHash.values()) {
                String type1 = pokemon.getType1();
                String type2 = pokemon.getType2();
                int count1 = typeCounter.containsKey(type1) ? typeCounter.get(type1) : 0;
                int count2 = typeCounter.containsKey(type2) ? typeCounter.get(type2) : 0;
                typeCounter.put(type1, count1 + 1);
                typeCounter.put(type2, count2 + 1);
            }

            final boolean[] hasUnique = {false};
            typeCounter.forEach((s, integer) -> {
                if (integer == 1) {
                    System.out.printf("%s is unique for %s #%d%n", s, formName, dexNumberLookup.get(formName));
                    hasUnique[0] = true;
                }
            });
            if (!hasUnique[0]) {
                System.out.println(formName + " has no unique typing. :((((((((((((((((((((((((((((((((((((((((((((((");
            }

        }
    }

    /**
     * Reads the V2_GAME_MASTER.json located in the project root directory, and returns a List&lt;ItemTemplate&gt;
     *     containing all the data contained in the Json.
     *
     * @return a List of all data contained in the Json
     */
    private static List<Data> getDataFromGamemasterJson() {
        URL url = null;
        try {
            url = new URL("https://raw.githubusercontent.com/pokemongo-dev-contrib/pokemongo-game-master/master"
                    + "/versions/latest/V2_GAME_MASTER.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        PogoJson json = new Gson().fromJson(reader, PogoJson.class);

        // in V2, json has an extra layer of nesting: { "template": [{"data": {"templateId": "foo", ...} }] }
        return json.getTemplates().stream().map(Template::getData).collect(Collectors.toList());
    }

    private static Map<Integer, List<String>> getNameMapFromCSV() {
        Map<Integer, List<String>> names = new HashMap<>();
        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(Paths.get("names.csv"), UTF_8);
            String line = reader.readLine();
            int dexNoIdx = -1;
            ArrayList<String> header = new ArrayList<>();
            if (line != null) {
                header.addAll(Arrays.asList(line.split(",")));
                dexNoIdx = header.indexOf("dexNo");
                if (dexNoIdx == -1) {
                    throw new RuntimeException("Couldn't find Dex # field (make sure the \"dexNo\" column exists)");
                }
            }
            while ((line = reader.readLine()) != null) {
                ArrayList<String> values = new ArrayList<>(Arrays.asList(line.split(",")));

                if (values.size() <= header.size()) { // Allow Missing names at end of list
                    int dexNo;
                    try {
                        dexNo = Integer.parseInt(values.get(dexNoIdx));
                    } catch (NumberFormatException nfe) {
                        continue;  // Pokemon is missing dex number, skip it
                    }

                    values.remove(dexNoIdx);
                    names.put(dexNo, values);
                }
            }

            header.remove(dexNoIdx);
            names.put(-1, header);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names;
    }

    private static void printPokemonXml(Map<Integer, List<String>> names, Set<Integer> dexNumbers) {
        int maxPokedex = Collections.max(dexNumbers);

        List<String> languages = names.get(-1);

        Map<String, List<String>> translations = new HashMap<>();
        for (String language : languages) {
            if (language != null) {
                translations.put(language, new ArrayList<>(dexNumbers.size()));
            }
        }

        final List<String> names_unknown_list = languages.stream().map(lang -> "Unknown").collect(Collectors.toList());

        for (int i = 1; i <= maxPokedex; i++) {
            if (dexNumbers.contains(i)) {
                List<String> namesForPokemon = names.get(i);
                if (namesForPokemon == null) {
                    namesForPokemon = names_unknown_list;
                // Pad any missing values with "Unknown"
                } else if (namesForPokemon.size() < languages.size()) {
                    namesForPokemon.addAll(names_unknown_list.subList(0, languages.size() - namesForPokemon.size()));
                }
                // Replace any blanks with "Unknown"
                namesForPokemon.replaceAll(name -> name.equals("") ? "Unknown" : name);

                for (int j = 0; j < languages.size(); j++) {
                    String language = languages.get(j);
                    if (language != null) {
                        translations.get(language).add(namesForPokemon.get(j));
                    }
                }
            }
        }

        for (String language : languages) {
            if (language != null) {
                StringBuilder contents = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                        + "<resources>\n    <string-array name=\"pokemon\">\n");
                Formatter formatter = new Formatter(contents);

                translations.get(language).forEach(name -> formatter.format("        <item>%s</item>\n", name));
                contents.append("    </string-array>\n</resources>");

                writeFile(String.format("pokemon-%s.xml", language), contents.toString());
            }
        }
    }

    /**
     * Prints out the contents that should be in Integers.xml
     *  @param formsByPokedex - Form data stored by NatDex number
     * @param pokemonWithMultipleForms - Form data for pokemon with multiple forms
     * @param pokemonFormsByName - Pokemon data by species and form ID
     * @param dexNumberLookup - Pokemon's dex # by name
     */
    private static void printIntegersXml(HashMap<Integer, FormSettings> formsByPokedex,
                                         ArrayList<FormSettings> pokemonWithMultipleForms,
                                         HashMap<String, HashMap<String, Pokemon>> pokemonFormsByName,
                                         HashMap<String, Integer> dexNumberLookup) {
        // Full file
        StringBuilder integersXmlBuilder = new StringBuilder();

        // Attack value of default form for each pokedex entry (no forms considered)
        StringBuilder attackBuilder = new StringBuilder();
        Formatter attackFormatter = new Formatter(attackBuilder);

        // Defense value of default form for each pokedex entry (no forms considered)
        StringBuilder defenseBuilder = new StringBuilder();
        Formatter defenseFormatter = new Formatter(defenseBuilder);

        // Stamina value of default form for each pokedex entry (no forms considered)
        StringBuilder staminaBuilder = new StringBuilder();
        Formatter staminaFormatter = new Formatter(staminaBuilder);

        // Parent evolution information for each pokedex entry (no forms considered)
        StringBuilder devolutionNumberBuilder = new StringBuilder();
        Formatter devolutionNumberFormatter = new Formatter(devolutionNumberBuilder);

        // Candy evolution cost of default form for each pokedex entry (no forms considered)
        StringBuilder evolutionCandyCostBuilder = new StringBuilder();
        Formatter evolutionCandyCostFormatter = new Formatter(evolutionCandyCostBuilder);

        // Candy name for each pokedex entry (no forms considered)
        StringBuilder candyNamesBuilder = new StringBuilder();
        Formatter candyNamesFormatter = new Formatter(candyNamesBuilder);

        StringBuilder formsCountIndexBuilder = new StringBuilder();
        Formatter formsCountIndexFormatter = new Formatter(formsCountIndexBuilder);

        // Seed initial text
        integersXmlBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
        attackBuilder.append("<integer-array name=\"attack\">\n");
        defenseBuilder.append("<integer-array name=\"defense\">\n");
        staminaBuilder.append("<integer-array name=\"stamina\">\n");
        evolutionCandyCostBuilder.append("<integer-array name=\"evolutionCandyCost\">\n");
        devolutionNumberBuilder.append("<integer-array name=\"devolutionNumber\">\n");
        candyNamesBuilder.append("<integer-array name=\"candyNames\">\n");
        formsCountIndexBuilder.append("<integer-array name=\"formsCountIndex\">\n");

        int maxPokedex = Collections.max(formsByPokedex.keySet());
        for (int i = 1; i <= maxPokedex; i++) {
            if (formsByPokedex.containsKey(i)) {
                FormSettings form = formsByPokedex.get(i);
                HashMap<String, Pokemon> formHash = pokemonFormsByName.get(form.getName());
                if (formHash != null) {
                    Pokemon poke = formHash.get(null);
                    String pokemonName = titleCase(form.getName());
                    Stats stats = poke.getStats();

                    attackFormatter.format(integerArrayFormat, stats.getBaseAttack())
                            .format(commentFormat, pokemonName);
                    defenseFormatter.format(integerArrayFormat, stats.getBaseDefense())
                            .format(commentFormat, pokemonName);
                    staminaFormatter.format(integerArrayFormat, stats.getBaseStamina())
                            .format(commentFormat, pokemonName);

                    // Devolution Number
                    devolutionNumberFormatter.format(integerArrayFormat, dexNumberLookup.get(poke.getParentId()) - 1);
                    devolutionNumberFormatter.format(commentFormat, pokemonName);

                    // Evolution Candy Cost
                    Integer evolveCandy = null;
                    if (poke.getEvolutionBranches() != null) {
                        evolveCandy = poke.getEvolutionBranches().get(0).getCandyCost();
                    }
                    if (evolveCandy == null) {
                        evolveCandy = poke.getCandyToEvolve();
                    }
                    if (evolveCandy == null) {
                        evolveCandy = -1;
                    }
                    evolutionCandyCostFormatter.format(integerArrayFormat, evolveCandy)
                            .format(commentFormat, pokemonName);

                    // Candy Names
                    candyNamesFormatter.format(integerArrayFormat,
                            dexNumberLookup.get(poke.getFamilyId().substring(7)) - 1
                    );
                    candyNamesFormatter.format(commentFormat, pokemonName);

                    // Forms Count Index
                    formsCountIndexFormatter.format(integerArrayFormat, pokemonWithMultipleForms.indexOf(form));
                    formsCountIndexFormatter.format(commentFormat, pokemonName);
                }  // Some pokemon have form data in the game, but not pokemon data??
            }
        }

        // Add the line to close all the xml arrays
        attackBuilder.append("</integer-array>\n");
        defenseBuilder.append("</integer-array>\n");
        staminaBuilder.append("</integer-array>\n");
        devolutionNumberBuilder.append("</integer-array>\n");
        evolutionCandyCostBuilder.append("</integer-array>\n");
        candyNamesBuilder.append("</integer-array>\n");
        formsCountIndexBuilder.append("</integer-array>\n");

        // Add all the xml arrays to the main XML
        integersXmlBuilder.append(attackBuilder.toString());
        integersXmlBuilder.append(defenseBuilder.toString());
        integersXmlBuilder.append(staminaBuilder.toString());
        integersXmlBuilder.append(devolutionNumberBuilder.toString());
        integersXmlBuilder.append(evolutionCandyCostBuilder.toString());
        integersXmlBuilder.append(candyNamesBuilder.toString());
        integersXmlBuilder.append(formsCountIndexBuilder.toString());

        // Finishing touches
        integersXmlBuilder.append("</resources>\n");

        //System.out.println(stringBuilder);
        writeFile("integers.xml", integersXmlBuilder.toString());
    }

    /**
     * Prints out all the contents that should be in forms.xml
     *
     * @param pokemonWithMultipleForms - Form data for pokemon with multiple forms
     * @param pokemonFormsByName - Pokemon data by species and form ID
     */
    private static void printFormsXml(ArrayList<FormSettings> pokemonWithMultipleForms,
                                      HashMap<String, HashMap<String, Pokemon>> pokemonFormsByName) {
        // Full file
        StringBuilder formsXmlBuilder = new StringBuilder();

        // The number of forms for each multi-form pokemon
        StringBuilder formsCountBuilder = new StringBuilder();
        Formatter formsCountFormatter = new Formatter(formsCountBuilder);

        // The form name of each form for each multi-form pokemon
        StringBuilder formNamesBuilder = new StringBuilder();
        Formatter formNamesFormatter = new Formatter(formNamesBuilder);

        // The attack value of each form for each multi-form pokemon
        StringBuilder formAttackBuilder = new StringBuilder();
        Formatter formAttackFormatter = new Formatter(formAttackBuilder);

        // The defense value of each form for each multi-form pokemon
        StringBuilder formDefenseBuilder = new StringBuilder();
        Formatter formDefenseFormatter = new Formatter(formDefenseBuilder);

        // The stamina value of each form for each multi-form pokemon
        StringBuilder formStaminaBuilder = new StringBuilder();
        Formatter formStaminaFormatter = new Formatter(formStaminaBuilder);

        // Seed initial text
        formsXmlBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
        formsCountBuilder.append("<integer-array name=\"formsCount\">\n");
        formNamesBuilder.append("<string-array name=\"formNames\">\n");
        formAttackBuilder.append("<integer-array name=\"formAttack\">\n");
        formDefenseBuilder.append("<integer-array name=\"formDefense\">\n");
        formStaminaBuilder.append("<integer-array name=\"formStamina\">\n");

        for (FormSettings form : pokemonWithMultipleForms) {
            String pokemonName = titleCase(form.getName());
            HashMap<String, Pokemon> formHash = pokemonFormsByName.get(form.getName());
            if (formHash != null) {
                formsCountFormatter.format(integerArrayFormat, form.getForms().size());
                formsCountFormatter.format(commentFormat, pokemonName);

                formNamesFormatter.format(commentFormat, pokemonName);
                formAttackFormatter.format(commentFormat, pokemonName);
                formDefenseFormatter.format(commentFormat, pokemonName);
                formStaminaFormatter.format(commentFormat, pokemonName);
                for (Form subform : form.getForms()) {
                    Pokemon poke = formHash.get(formHash.containsKey(subform.getForm()) ? subform.getForm() : null);
                    Stats stats = poke.getStats();
                    String formName = formName(subform.getForm(), form.getName());

                    // Form Names
                    formNamesFormatter.format(stringArrayFormat, formName);

                    // Form Stats
                    formAttackFormatter.format(integerArrayFormat, unnull(stats.getBaseAttack(), -1));
                    formAttackFormatter.format(commentFormat, formName);
                    formDefenseFormatter.format(integerArrayFormat, unnull(stats.getBaseDefense(), -1));
                    formDefenseFormatter.format(commentFormat, formName);
                    formStaminaFormatter.format(integerArrayFormat, unnull(stats.getBaseStamina(), -1));
                    formStaminaFormatter.format(commentFormat, formName);
                }
            }
        }

        // Add the line to close all the xml arrays
        formsCountBuilder.append("</integer-array>\n");
        formNamesBuilder.append("</string-array>\n");
        formAttackBuilder.append("</integer-array>\n");
        formDefenseBuilder.append("</integer-array>\n");
        formStaminaBuilder.append("</integer-array>\n");

        // Add all the xml arrays to the main XML
        formsXmlBuilder.append(formsCountBuilder.toString());
        formsXmlBuilder.append(formNamesBuilder.toString());
        formsXmlBuilder.append(formAttackBuilder.toString());
        formsXmlBuilder.append(formDefenseBuilder.toString());
        formsXmlBuilder.append(formStaminaBuilder.toString());

        // finishing touches
        formsXmlBuilder.append("</resources>\n");

        //System.out.println(stringBuilder);
        writeFile("forms.xml", formsXmlBuilder.toString());
    }

    /**
     * Converts a string like 'RAICHU_ALOLAN' to 'Alolan Form', or 'ARCEUS_DARK' to 'Dark Form'.
     *
     * @param form - Form name
     * @param pokemon - Pokemon Species name
     * @return a human readable description of the form
     */
    private static String formName(String form, String pokemon) {
        form = form.replaceFirst("MEWTWO_A", "MEWTWO_Armored"); // For some reason Mewtwo is weird
        String simpleName = form.substring(pokemon.length() + 1); // Skip past pokemon name and underscore
        return titleCase(simpleName) + " Form";
    }

    /**
     * Guarantees a non-null value.
     * @param nullish - value to check
     * @param fallback - backup in case the value is null
     * @param <T> - Value's Type
     * @return a non-null value
     */
    private static <T> T unnull(T nullish, T fallback) {
        return (nullish == null) ? fallback : nullish;
    }

    /**
     * Converts text such as "THIS IS annoyingly WEIRd CapitaLIZATION" to "This Is Annoyingly Weird Capitalization"
     * - capitalizing each word.
     *
     * @param str - annoying text
     * @return aesthetic text
     */
    private static String titleCase(String str) {
        return Arrays.stream(str.split("[ _]")).map(
                word -> Character.toTitleCase(word.charAt(0)) + word.substring(1).toLowerCase()
        ).collect(Collectors.joining(" "));
    }

    private static void writeFile(String fileName, String content) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), UTF_8))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
