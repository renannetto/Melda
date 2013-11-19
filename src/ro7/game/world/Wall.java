package ro7.game.world;

import java.awt.Graphics2D;
import java.util.Map;

import ro7.engine.sprites.shapes.CollidingShape;
import ro7.engine.world.Collision;
import ro7.engine.world.GameWorld;
import ro7.engine.world.entities.StaticEntity;

public class Wall extends StaticEntity {

	protected Wall(GameWorld world, CollidingShape shape, String name,
			Map<String, String> properties) {
		super(world, shape, name, properties);
	}
	
	@Override
	public void draw(Graphics2D g) {
		
	}
	
	@Override
	public void onCollision(Collision collision) {
		super.onCollision(collision);
	}

}