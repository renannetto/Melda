package ro7.game.world.enemies;

import java.util.Map;
import java.util.Set;

import ro7.engine.sprites.shapes.CollidingShape;
import ro7.engine.world.Collision;
import ro7.engine.world.GameWorld;
import ro7.engine.world.entities.MovingEntity;
import ro7.game.world.FinalEntity;
import ro7.game.world.player.Item;

public class Arrow extends MovingEntity implements FinalEntity {

	public Arrow(GameWorld world, CollidingShape shape, String name,
			Map<String, String> properties) {
		super(world, shape, name, properties);
	}

	@Override
	public void onCollision(Collision collision) {
		FinalEntity otherEntity = (FinalEntity) collision.other;
		otherEntity.receiveDamage(1);
		world.removeEntity(name);
	}

	@Override
	public void onCollisionDynamic(Collision collision) {

	}

	@Override
	public void onCollisionStatic(Collision collision) {

	}

	@Override
	public void receiveDamage(int damage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchEnemy(Collision collision) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveAttack(Collision collision) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveAction(Collision collision, Set<Item> inventory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getItem(Item item) {
		// TODO Auto-generated method stub
		
	}

}
