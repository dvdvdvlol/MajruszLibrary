package com.mlib.gamemodifiers.contexts;

import com.mlib.events.ItemSwingDurationEvent;
import com.mlib.gamemodifiers.ContextBase;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.Contexts;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

public class OnItemSwingDuration {
	@Mod.EventBusSubscriber
	public static class Context extends ContextBase< Data > {
		static final Contexts< Data, Context > CONTEXTS = new Contexts<>();

		public Context( Consumer< Data > consumer ) {
			super( consumer );

			CONTEXTS.add( this );
		}

		@SubscribeEvent
		public static void onItemSwingDuration( ItemSwingDurationEvent event ) {
			CONTEXTS.accept( new Data( event ) );
		}
	}

	public static class Data extends ContextData.Event< ItemSwingDurationEvent > {
		public final LivingEntity entity;

		public Data( ItemSwingDurationEvent event ) {
			super( event.entity, event );
			this.entity = event.entity;
		}
	}
}