package com.mlib.gamemodifiers.contexts;

import com.google.gson.JsonObject;
import com.mlib.ObfuscationGetter;
import com.mlib.gamemodifiers.ContextBase;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.Contexts;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

public class OnLootTableCustomLoad {
	public static class Context extends ContextBase< Data > {
		static final Contexts< Data, Context > CONTEXTS = new Contexts<>();

		public Context( Consumer< Data > consumer ) {
			super( consumer );

			CONTEXTS.add( this );
		}

		public static void broadcast( Data data ) {
			CONTEXTS.accept( data );
		}
	}

	public static class Data extends ContextData {
		static final ObfuscationGetter.Field< LootTable, List< LootPool > > POOLS = new ObfuscationGetter.Field<>( LootTable.class, "f_79109_" );
		static final ObfuscationGetter.Field< LootPool, LootPoolEntryContainer[] > ENTRIES = new ObfuscationGetter.Field<>( LootPool.class, "f_79023_" );
		public final ResourceLocation name;
		public final LootTable table;
		public final List< LootPool > pools;
		public final JsonObject jsonObject;

		public Data( ResourceLocation name, LootTable table, JsonObject jsonObject ) {
			super( ( LivingEntity )null );
			this.name = name;
			this.table = table;
			this.pools = POOLS.get( table );
			this.jsonObject = jsonObject;
		}

		public void addEntry( int poolId, Item item, int weight, int quality, LootItemCondition.Builder... conditions ) {
			LootPool lootPool = this.pools.get( poolId );
			LootPoolEntryContainer[] entries = ENTRIES.get( lootPool );
			assert entries != null;
			LootPoolEntryContainer[] newEntries = new LootPoolEntryContainer[ entries.length + 1 ];
			System.arraycopy( entries, 0, newEntries, 0, entries.length );
			var builder = LootItem.lootTableItem( item ).setWeight( weight ).setQuality( quality );
			for( LootItemCondition.Builder condition : conditions ) {
				builder.when( condition );
			}
			newEntries[ entries.length ] = builder.build();
			ENTRIES.set( lootPool, newEntries );
		}

		public int addPool() {
			this.pools.add( LootPool.lootPool().name( String.format( "custom#%s%d", this.name.toString(), this.pools.size() ) ).build() );

			return this.pools.size() - 1;
		}
	}
}
