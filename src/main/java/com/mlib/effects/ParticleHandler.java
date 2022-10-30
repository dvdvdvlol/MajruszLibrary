package com.mlib.effects;

import com.mlib.math.VectorHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class ParticleHandler {
	public static final ParticleHandler AWARD = new ParticleHandler( ParticleTypes.HAPPY_VILLAGER, offset( 0.25f ), speed( 0.1f ) );
	public static final ParticleHandler ENCHANTED_HIT = new ParticleHandler( ParticleTypes.ENCHANTED_HIT, offset( 0.25f ), speed( 0.0f ) );
	public static final ParticleHandler SMELT = new ParticleHandler( ParticleTypes.FLAME, offset( 0.1f ), speed( 0.01f ) );

	final SimpleParticleType particleType;
	final Supplier< Vec3 > offsetProvider;
	final Supplier< Float > speedProvider;

	public static Supplier< Vec3 > offset( float value ) {
		return ()->new Vec3( value, value, value );
	}

	public static Supplier< Float > speed( float value ) {
		return ()->value;
	}

	public ParticleHandler( SimpleParticleType particleType, Supplier< Vec3 > offsetProvider, Supplier< Float > speedProvider ) {
		this.particleType = particleType;
		this.offsetProvider = offsetProvider;
		this.speedProvider = speedProvider;
	}

	@Deprecated
	public ParticleHandler( SimpleParticleType particleType, Vec3 offsetProvider, Supplier< Float > speedProvider ) {
		this( particleType, ()->offsetProvider, speedProvider );
	}

	public void spawn( ServerLevel level, Vec3 position, int amountOfParticles, Supplier< Vec3 > offsetProvider, Supplier< Float > speedProvider ) {
		Vec3 offset = offsetProvider.get();
		level.sendParticles( this.particleType, position.x, position.y, position.z, amountOfParticles, offset.x, offset.y, offset.z, speedProvider.get() );
	}

	public void spawn( ServerLevel level, Vec3 position, int amountOfParticles, Supplier< Vec3 > offsetProvider ) {
		this.spawn( level, position, amountOfParticles, offsetProvider, this.speedProvider );
	}

	public void spawn( ServerLevel level, Vec3 position, int amountOfParticles ) {
		this.spawn( level, position, amountOfParticles, this.offsetProvider, this.speedProvider );
	}

	@Deprecated
	public void spawn( ServerLevel level, Vec3 position, int amountOfParticles, double offsetMultiplier ) {
		this.spawn( level, position, amountOfParticles, ()->VectorHelper.multiply( this.offsetProvider.get(), offsetMultiplier ), this.speedProvider );
	}

	public void spawnLine( ServerLevel level, Vec3 from, Vec3 to, int amountOfParticles, Supplier< Vec3 > offsetProvider, Supplier< Float > speedProvider ) {
		for( int i = 0; i <= amountOfParticles; i++ ) {
			Vec3 step = VectorHelper.add( from, VectorHelper.multiply( to, ( float )( i ) / amountOfParticles ) );
			this.spawn( level, step, 1, offsetProvider, speedProvider );
		}
	}

	public void spawnLine( ServerLevel level, Vec3 from, Vec3 to, int amountOfParticles, Supplier< Vec3 > offsetProvider ) {
		this.spawnLine( level, from, to, amountOfParticles, offsetProvider, this.speedProvider );
	}

	public void spawnLine( ServerLevel level, Vec3 from, Vec3 to, int amountOfParticles ) {
		this.spawnLine( level, from, to, amountOfParticles, this.offsetProvider, this.speedProvider );
	}
}
