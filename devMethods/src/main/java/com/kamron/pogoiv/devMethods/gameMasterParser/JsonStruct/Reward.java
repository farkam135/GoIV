package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Reward {
    static public class Item {
        @Expose
        private Boolean stardust = false;
        @Expose
        private Integer count;
        @Expose
        private String item;

        public Boolean getStardust() { return stardust; }

        public void setStardust(Boolean stardust) { this.stardust = stardust; }

        public Integer getCount() { return count; }

        public void setCount(Integer count) { this.count = count; }

        public String getItem() { return item; }

        public void setItem(String item) { this.item = item; }
    }

    @Expose
    private Item item;
    @Expose
    private Boolean itemLootTable = false;
    @Expose
    private Boolean pokemonReward = false;

    public Item getItem() { return item; }

    public void setItem(Item item) { this.item = item; }

    public Boolean getItemLootTable() { return itemLootTable; }

    public void setItemLootTable(Boolean itemLootTable) { this.itemLootTable = itemLootTable; }

    public Boolean getPokemonReward() { return pokemonReward; }

    public void setPokemonReward(Boolean pokemonReward) { this.pokemonReward = pokemonReward; }
}
