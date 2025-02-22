package com.mlib.time;

import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleConfig;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.contexts.OnClientTick;
import com.mlib.gamemodifiers.contexts.OnServerTick;
import com.mlib.gamemodifiers.parameters.Priority;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nonnegative;

public class TimeHelper {
	static long clientCounter = 1;
	static long serverCounter = 1;

	public static boolean isEndPhase( TickEvent event ) {
		return event.phase == TickEvent.Phase.END;
	}

	public static < Type extends TickEvent > boolean isEndPhase( ContextData.Event< Type > data ) {
		return isEndPhase( data.event );
	}

	public static boolean hasClientTicksPassed( @Nonnegative int ticks ) {
		return clientCounter % ticks == 0;
	}

	public static boolean hasClientSecondsPassed( @Nonnegative double seconds ) {
		return hasClientTicksPassed( Utility.secondsToTicks( seconds ) );
	}

	public static boolean hasClientSecondsPassed( DoubleConfig config ) {
		return hasClientTicksPassed( config.asTicks() );
	}

	public static long getClientTicks() {
		return clientCounter;
	}

	public static boolean hasServerTicksPassed( @Nonnegative int ticks ) {
		return serverCounter % ticks == 0;
	}

	public static boolean hasServerSecondsPassed( @Nonnegative double seconds ) {
		return hasServerTicksPassed( Utility.secondsToTicks( seconds ) );
	}

	public static boolean hasServerSecondsPassed( DoubleConfig config ) {
		return hasServerTicksPassed( config.asTicks() );
	}

	public static long getServerTicks() {
		return serverCounter;
	}

	@AutoInstance
	public static class Updater {
		public Updater() {
			new OnClientTick.Context( data->++clientCounter )
				.priority( Priority.HIGHEST )
				.addCondition( TimeHelper::isEndPhase );

			new OnServerTick.Context( data->++serverCounter )
				.priority( Priority.HIGHEST )
				.addCondition( TimeHelper::isEndPhase );
		}
	}
}
