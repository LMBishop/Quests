package com.leonardobishop.quests.blocktype;

import org.bukkit.Material;

@Deprecated
public class Block {

    private Material material;
    private short data;

    public Block(Material material, short data) {
        this.material = material;
        this.data = data;
    }

    public Block(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public short getData() {
        return data;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setData(short data) {
        this.data = data;
    }
}
