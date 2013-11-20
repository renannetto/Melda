package ro7.game.world;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import ro7.engine.sprites.AnimatedSprite;
import ro7.engine.sprites.ImageSprite;
import ro7.engine.sprites.SpriteSheet;
import ro7.engine.sprites.shapes.AAB;
import ro7.engine.sprites.shapes.CollidingShape;
import ro7.engine.sprites.shapes.CollidingSprite;
import ro7.engine.world.Collision;
import ro7.engine.world.GameWorld;
import ro7.engine.world.io.Connection;
import ro7.engine.world.io.Input;
import ro7.game.world.enemies.Enemy;
import cs195n.Vec2f;
import cs195n.Vec2i;

public class Player extends Character {

	private String attackCategory;
	private String attackCollision;
	private Attack currentAttack;

	private Map<Vec2f, ImageSprite> standing;
	private AnimatedSprite walkingDown;
	private AnimatedSprite walkingUp;
	private AnimatedSprite walkingRight;
	private AnimatedSprite walkingLeft;
	private AnimatedSprite attackingDown;
	private AnimatedSprite attackingUp;
	private AnimatedSprite attackingRight;
	private AnimatedSprite attackingLeft;

	public Player(GameWorld world, CollidingShape shape, String name,
			Map<String, String> properties) {
		super(world, shape, name, properties);
		if (properties.containsKey("attackCategory")) {
			attackCategory = properties.get("attackCategory");
		} else {
			attackCategory = "-1";
		}
		if (properties.containsKey("attackCollision")) {
			attackCollision = properties.get("attackCollision");
		} else {
			attackCollision = "-1";
		}
		
		inputs.put("doStopAttack", new Input() {
			
			@Override
			public void run(Map<String, String> args) {
				currentAttack = null;
			}
		});
		
		SpriteSheet walkingSheet = world.getSpriteSheet(properties
				.get("walkingSheet"));

		standing = new HashMap<Vec2f, ImageSprite>();
		Vec2i posDown = new Vec2i(Integer.parseInt(properties
				.get("posDownX")), Integer.parseInt(properties
				.get("posDownY")));
		Vec2i posUp = new Vec2i(Integer.parseInt(properties
				.get("posUpX")), Integer.parseInt(properties
				.get("posUpY")));
		Vec2i posRight = new Vec2i(Integer.parseInt(properties
				.get("posRightX")), Integer.parseInt(properties
				.get("posRightY")));
		Vec2i posLeft = new Vec2i(Integer.parseInt(properties
				.get("posLeftX")), Integer.parseInt(properties
				.get("posLeftY")));
		standing.put(new Vec2f(0.0f, 1.0f), new ImageSprite(
				shape.getPosition(), walkingSheet, posDown));
		standing.put(new Vec2f(0.0f, -1.0f), new ImageSprite(
				shape.getPosition(), walkingSheet, posUp));
		standing.put(new Vec2f(1.0f, 0.0f), new ImageSprite(
				shape.getPosition(), walkingSheet, posRight));
		standing.put(new Vec2f(-1.0f, 0.0f), new ImageSprite(
				shape.getPosition(), walkingSheet, posLeft));

		int framesWalking = Integer.parseInt(properties.get("framesWalking"));
		float timeToMoveWalking = Float.parseFloat(properties
				.get("timeToMoveWalking"));
		walkingDown = new AnimatedSprite(shape.getPosition(), walkingSheet,
				posDown, framesWalking, timeToMoveWalking);
		walkingUp = new AnimatedSprite(shape.getPosition(), walkingSheet,
				posUp, framesWalking, timeToMoveWalking);
		walkingRight = new AnimatedSprite(shape.getPosition(), walkingSheet,
				posRight, framesWalking, timeToMoveWalking);
		walkingLeft = new AnimatedSprite(shape.getPosition(), walkingSheet,
				posLeft, framesWalking, timeToMoveWalking);
		
		SpriteSheet attackingSheet = world.getSpriteSheet(properties
				.get("attackingSheet"));
		int framesAttacking = Integer.parseInt(properties.get("framesAttacking"));
		float timeToMoveAttacking = Float.parseFloat(properties
				.get("timeToMoveAttacking"));
		attackingDown = new AnimatedSprite(shape.getPosition(), attackingSheet, posDown, framesAttacking, timeToMoveAttacking);
		attackingUp = new AnimatedSprite(shape.getPosition(), attackingSheet, posUp, framesAttacking, timeToMoveAttacking);
		attackingRight = new AnimatedSprite(shape.getPosition(), attackingSheet, posRight, framesAttacking, timeToMoveAttacking);
		attackingLeft = new AnimatedSprite(shape.getPosition(), attackingSheet, posLeft, framesAttacking, timeToMoveAttacking);
	}

	@Override
	public void update(long nanoseconds) {
		super.update(nanoseconds);
		updateSprite();
		this.shape.update(nanoseconds);
	}
	
	public void updateSprite() {
		if (currentAttack != null) {
			if (direction.y > 0) {
				((CollidingSprite) shape).updateSprite(attackingDown);
			} else if (direction.y < 0) {
				((CollidingSprite) shape).updateSprite(attackingUp);
			} else if (direction.x > 0) {
				((CollidingSprite) shape).updateSprite(attackingRight);
			} else {
				((CollidingSprite) shape).updateSprite(attackingLeft);
			}
			currentAttack.moveTo(getAttackPosition());
		} else if (velocity.y > 0) {
			((CollidingSprite) shape).updateSprite(walkingDown);
		} else if (velocity.y < 0) {
			((CollidingSprite) shape).updateSprite(walkingUp);
		} else if (velocity.x > 0) {
			((CollidingSprite) shape).updateSprite(walkingRight);
		} else if (velocity.x < 0) {
			((CollidingSprite) shape).updateSprite(walkingLeft);
		}
		else {
			((CollidingSprite) shape).updateSprite(standing.get(direction));
		}
	}

	public Attack attack(String name) {
		if (currentAttack!=null) {
			return currentAttack;
		}
		
		Map<String, String> attackProperties = new HashMap<String, String>();
		attackProperties.put("categoryMask", attackCategory);
		attackProperties.put("collisionMask", attackCollision);
		attackProperties.put("damage", "1");

		Vec2f attackPosition = getAttackPosition();
		CollidingShape attackShape = new AAB(attackPosition, Color.BLUE,
				Color.BLUE, shape.getDimensions());
		currentAttack = new Attack(world, attackShape, name, attackProperties);
		
		Connection connection = new Connection(inputs.get("doStopAttack"), new HashMap<String, String>());
		currentAttack.connect("onFinish", connection);
		
		return currentAttack;
	}

	private Vec2f getAttackPosition() {
		Vec2f attackDirection = direction;
		if (Math.abs(attackDirection.y) >= Math.abs(attackDirection.x)) {
			attackDirection = new Vec2f(0.0f, attackDirection.y).normalized();
		} else {
			attackDirection = new Vec2f(attackDirection.x, 0.0f).normalized();
		}
		Vec2f attackPosition = shape.getPosition().plus(
				shape.getDimensions().pmult(attackDirection));
		return attackPosition;
	}

	public Vec2f getPosition() {
		return shape.getPosition();
	}

	@Override
	public void onCollision(Collision collision) {
		super.onCollision(collision);
		if (collision.other instanceof Enemy) {
			receiveDamage(1);
			Vec2f mtv = collision.mtv;
			Vec2f centerDistance = collision.thisShape.center().minus(
					collision.otherShape.center());
			if (mtv.dot(centerDistance) < 0) {
				mtv = mtv.smult(-1.0f);
			}
			push(mtv);
		}
	}

	@Override
	public void receiveDamage(int damage) {
		super.receiveDamage(damage);
		((FinalWorld) world).decreaseLife();
		if (lives <= 0) {
			((FinalWorld) world).lose();
		}
	}

}
