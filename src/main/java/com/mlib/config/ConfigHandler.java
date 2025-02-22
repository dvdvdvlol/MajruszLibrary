package com.mlib.config;

import com.mlib.events.ConfigsLoadedEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/** Handler that makes creating new configs much easier. */
public class ConfigHandler extends ConfigGroup {
	final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
	final ModConfig.Type type;
	ForgeConfigSpec configSpec = null;

	public ConfigHandler( ModConfig.Type type ) {
		this.type = type;
	}

	/** Registers all configs (all config values are valid after this call). */
	public void register( final ModLoadingContext modLoadingContext ) {
		this.build( this.builder );

		this.configSpec = this.builder.build();
		modLoadingContext.registerConfig( this.type, this.configSpec );
		MinecraftForge.EVENT_BUS.post( new ConfigsLoadedEvent( this ) );
		if( this.type == ModConfig.Type.SERVER && this.configSpec.size() > 0 ) {
			this.registerHelpConfigSpec( modLoadingContext );
		}
	}

	public ModConfig.Type getType() {
		return this.type;
	}

	private void registerHelpConfigSpec( final ModLoadingContext modLoadingContext ) {
		String[] comments = {
			" Hello, Majrusz over here!",
			" Since one of 1.19.2 release, configs used by my mods are mostly server-side",
			" which means they are stored separately per world. Thanks to this change,",
			" configuration files are synchronised with other players on the server, which",
			" is crucial for some of the functionalities.",
			"  ",
			" Anyway, here are the exact locations for 'true' configuration files:",
			"   Singleplayer: %appdata%/saves/<world>/serverconfig",
			"   Multiplayer: <your server>/<world>/serverconfig",
			"  ",
			" If you have any questions or want to see some more configs, feel",
			" free to contact me on my Discord server! Hope to see you there :D"
		};

		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment( comments ).define( "discord_url", "https://discord.gg/9UF774WcuW" );
		modLoadingContext.registerConfig( ModConfig.Type.COMMON, builder.build() );
	}
}
