package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.NMSVersion;
import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.type.Switch;

public class MaterialSwitchFace implements Property {

    public static boolean describes(ObjectTag material) {
        // TODO: After 1.14 is dropped, remove Switch entirely
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && (((MaterialTag) material).getModernData().data instanceof Switch
                || (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_15) && ((MaterialTag) material).getModernData().data instanceof FaceAttachable));
    }

    public static MaterialSwitchFace getFrom(ObjectTag _material) {
        if (!describes(_material)) {
            return null;
        }
        else {
            return new MaterialSwitchFace((MaterialTag) _material);
        }
    }

    public static final String[] handledMechs = new String[] {
            "switch_face"
    };

    private MaterialSwitchFace(MaterialTag _material) {
        material = _material;
    }

    MaterialTag material;

    public static void registerTags() {

        // <--[tag]
        // @attribute <MaterialTag.switch_face>
        // @returns ElementTag
        // @mechanism MaterialTag.switch_face
        // @group properties
        // @description
        // Returns the current attach direction for a switch or other attachable material.
        // Output is "CEILING", "FLOOR", or "WALL".
        // -->
        PropertyParser.<MaterialSwitchFace>registerTag("switch_face", (attribute, material) -> {
            if (material.isSwitch()) {
                return new ElementTag(material.getSwitch().getFace().name());
            }
            else {
                return new ElementTag(material.getFaceAttachable().getAttachedFace().name());
            }
        });
    }

    public boolean isSwitch() {
        return material.getModernData().data instanceof Switch;
    }

    public Switch getSwitch() {
        return (Switch) material.getModernData().data;
    }

    public FaceAttachable getFaceAttachable() {
        return (FaceAttachable) material.getModernData().data;
    }

    @Override
    public String getPropertyString() {
        if (isSwitch()) {
            return getSwitch().getFace().name();
        }
        else {
            return getFaceAttachable().getAttachedFace().name();
        }
    }

    public BlockFace getAttachedTo() {
        if (isSwitch()) {
            switch (getSwitch().getFace()) {
                case WALL:
                    return getSwitch().getFacing().getOppositeFace();
                case FLOOR:
                    return BlockFace.DOWN;
                case CEILING:
                    return BlockFace.UP;
                default:
                    return BlockFace.SELF;
            }
        }
        else {
            switch (getFaceAttachable().getAttachedFace()) {
                case WALL:
                    if (material.getModernData().data instanceof Directional) {
                        return ((Directional) material.getModernData().data).getFacing().getOppositeFace();
                    }
                case FLOOR:
                    return BlockFace.DOWN;
                case CEILING:
                    return BlockFace.UP;
                default:
                    return BlockFace.SELF;
            }
        }
    }

    @Override
    public String getPropertyId() {
        return "switch_face";
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name switch_face
        // @input ElementTag
        // @description
        // Sets the current attach direction for a switch or other attachable material.
        // @tags
        // <MaterialTag.switch_face>
        // -->
        if (mechanism.matches("switch_face") && mechanism.requireEnum(false, Switch.Face.values())) {
            if (isSwitch()) {
                getSwitch().setFace(Switch.Face.valueOf(mechanism.getValue().asString().toUpperCase()));
            }
            else {
                getFaceAttachable().setAttachedFace(FaceAttachable.AttachedFace.valueOf(mechanism.getValue().asString().toUpperCase()));
            }
        }
    }
}
