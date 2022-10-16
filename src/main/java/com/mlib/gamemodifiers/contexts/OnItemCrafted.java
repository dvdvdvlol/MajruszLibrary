package com.mlib.gamemodifiers.contexts;

import com.mlib.gamemodifiers.ContextBase;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.parameters.ContextParameters;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class OnItemCrafted {
	@Mod.EventBusSubscriber
	public static class Context extends ContextBase< Data > {
		static final List< Context > CONTEXTS = Collections.synchronizedList( new ArrayList<>() );

		public Context( Consumer< Data > consumer, ContextParameters params ) {
			super( Data.class, consumer, params );
			ContextBase.addSorted( CONTEXTS, this );
		}

		public Context( Consumer< Data > consumer ) {
			this( consumer, new ContextParameters() );
		}

		@SubscribeEvent
		public static void onItemCrafted( PlayerEvent.ItemCraftedEvent event ) {
			ContextBase.accept( CONTEXTS, new Data( event ) );
		}
	}

	public static class Data extends ContextData.Event< PlayerEvent.ItemCraftedEvent > {
		public final Player player;
		public final ItemStack itemStack;
		public final Container container;

		public Data( PlayerEvent.ItemCraftedEvent event ) {
			super( event.getEntity(), event );
			this.player = event.getEntity();
			this.itemStack = event.getCrafting();
			this.container = event.getInventory();
		}
	}
}
