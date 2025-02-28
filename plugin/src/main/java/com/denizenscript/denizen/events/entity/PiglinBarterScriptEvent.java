package com.denizenscript.denizen.events.entity;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;

public class PiglinBarterScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // piglin barter
    //
    // @Switch input:<item> to only process the event if the input item matches the given item matcher.
    //
    // @Group Entity
    //
    // @Location true
    //
    // @Cancellable true
    //
    // @Triggers when a piglin picks up an item for bartering.
    //
    // @Context
    // <context.entity> returns the EntityTag of the piglin.
    // <context.input> returns the ItemTag of the input item.
    // <context.outcome> returns a ListTag(ItemTag) of outcome itemes.
    //
    // -->

    public PiglinBarterScriptEvent() {
        registerCouldMatcher("piglin barter");
    }

    public EntityTag entity;
    public PiglinBarterEvent event;

    @Override
    public boolean matches(ScriptPath path) {
        if (!runInCheck(path, entity.getLocation())) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        switch (name) {
            case "entity": return entity;
            case "input": return new ItemTag(event.getInput());
            case "outcome": {
                ListTag result = new ListTag();
                for (ItemStack item : event.getOutcome()) {
                    result.addObject(new ItemTag(item));
                }
                return result;
            }
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onBarter(PiglinBarterEvent event) {
        entity = new EntityTag(event.getEntity());
        this.event = event;
        fire(event);
    }
}
