package com.mlib.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Deprecated( since = "3.2.0 (use ConfigurableParticle instead)", forRemoval = true )
@OnlyIn( Dist.CLIENT )
public class SimpleParticle extends TextureSheetParticle {
	protected float yOffset = 0.0f;
	protected boolean renderUpwardsWhenOnGround = false;
	protected IFormula< Double > xdFormula = xd->xd * 0.95;
	protected IFormula< Double > ydFormula = yd->yd - 0.0375;
	protected IFormula< Double > zdFormula = zd->zd * 0.95;
	protected IFormula< Double > xdOnGroundFormula = xd->xd * 0.5;
	protected IFormula< Double > ydOnGroundFormula = yd->yd;
	protected IFormula< Double > zdOnGroundFormula = zd->zd * 0.5;
	protected IFormula< Float > alphaFormula = alpha->alpha;
	protected IFormula< Float > scaleFormula = lifeRatio->1.0f - 0.5f * lifeRatio;

	public SimpleParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet,
		double yOffset
	) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );
		this.yOffset = ( float )yOffset;
	}

	public SimpleParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if( ++this.age >= this.lifetime ) {
			remove();
		} else {
			this.move( this.xd, this.yd, this.zd );
			this.xd = this.xdFormula.apply( this.xd );
			this.yd = this.ydFormula.apply( this.yd );
			this.zd = this.zdFormula.apply( this.zd );
			if( this.onGround ) {
				this.xd = this.xdOnGroundFormula.apply( this.xd );
				this.yd = this.ydOnGroundFormula.apply( this.yd );
				this.zd = this.zdOnGroundFormula.apply( this.zd );
			}
			this.alpha = this.alphaFormula.apply( this.alpha );
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void render( VertexConsumer vertexConsumer, Camera camera, float scaleFactor ) {
		// copied from SingleQuadParticle with few modifiable variables:
		// - f1 to render slightly over surface
		// - quaternion to render upwards instead towards the camera
		// TODO: use mixin

		Vec3 vec3 = camera.getPosition();
		float f = ( float )( Mth.lerp( scaleFactor, this.xo, this.x ) - vec3.x() );
		float f1 = ( float )( Mth.lerp( scaleFactor, this.yo + this.yOffset, this.y + this.yOffset ) - vec3.y() );
		float f2 = ( float )( Mth.lerp( scaleFactor, this.zo, this.z ) - vec3.z() );
		Quaternionf quaternion;
		if( this.roll == 0.0F ) {
			quaternion = this.renderUpwardsWhenOnGround && this.onGround ? Axis.XP.rotation( Mth.HALF_PI ) : camera.rotation();
		} else {
			quaternion = new Quaternionf( camera.rotation() );
			quaternion.rotateZ( Mth.lerp( scaleFactor, this.oRoll, this.roll ) );
		}

		Vector3f vector3f1 = new Vector3f( -1.0F, -1.0F, 0.0F );
		vector3f1.rotate( quaternion );
		Vector3f[] avector3f = new Vector3f[]{
			new Vector3f( -1.0F, -1.0F, 0.0F ), new Vector3f( -1.0F, 1.0F, 0.0F ), new Vector3f( 1.0F, 1.0F, 0.0F ), new Vector3f( 1.0F, -1.0F, 0.0F )
		};
		float f4 = this.getQuadSize( scaleFactor );

		for( int i = 0; i < 4; ++i ) {
			Vector3f vector3f = avector3f[ i ];
			vector3f.rotate( quaternion );
			vector3f.mul( f4 );
			vector3f.add( f, f1, f2 );
		}

		float f7 = this.getU0();
		float f8 = this.getU1();
		float f5 = this.getV0();
		float f6 = this.getV1();
		int j = this.getLightColor( scaleFactor );
		vertexConsumer.vertex( ( double )avector3f[ 0 ].x(), ( double )avector3f[ 0 ].y(), ( double )avector3f[ 0 ].z() )
			.uv( f8, f6 )
			.color( this.rCol, this.gCol, this.bCol, this.alpha )
			.uv2( j )
			.endVertex();
		vertexConsumer.vertex( ( double )avector3f[ 1 ].x(), ( double )avector3f[ 1 ].y(), ( double )avector3f[ 1 ].z() )
			.uv( f8, f5 )
			.color( this.rCol, this.gCol, this.bCol, this.alpha )
			.uv2( j )
			.endVertex();
		vertexConsumer.vertex( ( double )avector3f[ 2 ].x(), ( double )avector3f[ 2 ].y(), ( double )avector3f[ 2 ].z() )
			.uv( f7, f5 )
			.color( this.rCol, this.gCol, this.bCol, this.alpha )
			.uv2( j )
			.endVertex();
		vertexConsumer.vertex( ( double )avector3f[ 3 ].x(), ( double )avector3f[ 3 ].y(), ( double )avector3f[ 3 ].z() )
			.uv( f7, f6 )
			.color( this.rCol, this.gCol, this.bCol, this.alpha )
			.uv2( j )
			.endVertex();
	}

	@Override
	public float getQuadSize( float scaleFactor ) {
		return this.quadSize * this.scaleFormula.apply( ( ( float )this.age + scaleFactor ) / ( float )this.lifetime );
	}

	@OnlyIn( Dist.CLIENT )
	public static abstract class SimpleFactory implements ParticleProvider< SimpleParticleType > {
		private final SpriteSet spriteSet;
		private final IInstanceFactory instanceFactory;

		public SimpleFactory( SpriteSet sprite, IInstanceFactory instanceFactory ) {
			this.spriteSet = sprite;
			this.instanceFactory = instanceFactory;
		}

		@Override
		public Particle createParticle( SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed,
			double zSpeed
		) {
			return this.instanceFactory.createInstance( world, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet );
		}

		@FunctionalInterface
		public interface IInstanceFactory {
			SimpleParticle createInstance( ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
				SpriteSet spriteSet
			);
		}
	}

	@FunctionalInterface
	public interface IFormula< Type > {
		Type apply( Type type );
	}
}
