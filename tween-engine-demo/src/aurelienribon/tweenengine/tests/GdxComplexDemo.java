package aurelienribon.tweenengine.tests;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenaccessors.gdx.SpriteAccessor;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Locale;

public class GdxComplexDemo implements ApplicationListener {
	public static void start() {
		new LwjglApplication(new GdxComplexDemo(), "", 500, 200, false);
	}

	private OrthographicCamera camera;
	private SpriteBatch sb;
	private TweenManager tweenManager;

	private BitmapFont font;
	private String text;

	private Sprite sprite1;
	private Sprite sprite2;
	private Sprite sprite3;
	private Sprite sprite4;

	private boolean canBeRestarted = false;
	private boolean canControlSpeed = false;
	private float speed = 1;
	private int iterationCnt;

	@Override
	public void create() {
		// Input manager
		Gdx.input.setInputProcessor(inputProcessor);

		// Camera + Spritebatch + font
		float ratio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		this.camera = new OrthographicCamera(6, 6/ratio);
		this.sb = new SpriteBatch();
		this.font = new BitmapFont();
		font.setColor(Color.BLACK);

		// Creation of the sprites, classic way
		Texture tex1 = new Texture(Gdx.files.internal("data/logo1.png"));
		Texture tex2 = new Texture(Gdx.files.internal("data/logo2.png"));
		Texture tex3 = new Texture(Gdx.files.internal("data/logo3.png"));
		Texture tex4 = new Texture(Gdx.files.internal("data/logo4.png"));
		tex1.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		tex2.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		tex3.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		tex4.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		this.sprite1 = new Sprite(tex1);
		this.sprite2 = new Sprite(tex2);
		this.sprite3 = new Sprite(tex3);
		this.sprite4 = new Sprite(tex4);
		sprite1.setSize(1, 1);
		sprite2.setSize(1, 1);
		sprite3.setSize(1, 1);
		sprite4.setSize(1, 1);
		sprite1.setOrigin(0.5f, 0.5f);
		sprite2.setOrigin(0.5f, 0.5f);
		sprite3.setOrigin(0.5f, 0.5f);
		sprite4.setOrigin(0.5f, 0.5f);
		sprite1.setPosition((6f/5f)*1 - 3 - 0.5f, -0.5f);
		sprite2.setPosition((6f/5f)*2 - 3 - 0.5f, -0.5f);
		sprite3.setPosition((6f/5f)*3 - 3 - 0.5f, -0.5f);
		sprite4.setPosition((6f/5f)*4 - 3 - 0.5f, -0.5f);
		sprite1.setColor(1, 1, 1, 0);
		sprite2.setColor(1, 1, 1, 0);
		sprite3.setColor(1, 1, 1, 0);
		sprite4.setColor(1, 1, 1, 0);

		// Tween engine setup
		Tween.enablePooling(true);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		// Tween manager creation
		tweenManager = new TweenManager();

		// Demo of the Tween.call possibility. It's just a timer :)
		text = "Idle (auto-start in 2 seconds)";
		Tween.call(new TweenCallback() {
			@Override public void tweenEventOccured(Types eventType, Tween tween) {
				launchAnimation();
				canControlSpeed = true;
			}
		}).delay(2000).start(tweenManager);
	}

	@Override
	public void render() {
		// Remember to update the tween manager periodically!
		int delta = (int) (Gdx.graphics.getDeltaTime() * 1000 * speed);
		tweenManager.update(delta);

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(gl.GL_COLOR_BUFFER_BIT);

		sb.setProjectionMatrix(camera.combined);
		sb.begin();
		sprite1.draw(sb);
		sprite2.draw(sb);
		sprite3.draw(sb);
		sprite4.draw(sb);
		sb.end();

		sb.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sb.begin();
		font.setColor(0.5f, 0.5f, 0.5f, 1);
		font.draw(sb, "Move your mouse over the screen to change the animation speed", 5, Gdx.graphics.getHeight());
		font.draw(sb, String.format(Locale.US, "Current speed: %.2f", speed), 5, Gdx.graphics.getHeight() - 20);
		font.draw(sb, text, 5, 45);
		font.draw(sb, "Tweens in pool: " + Tween.getPoolSize(), 150, 25);
		font.draw(sb, "Running tweens: " + tweenManager.size(), 5, 25);
		sb.end();
	}

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}

	// -------------------------------------------------------------------------
	// ANIMATION
	// -------------------------------------------------------------------------

	private Timeline buildAnimation(Sprite target, int delay1, int delay2) {
		return Timeline.createSequence()
			.push(Tween.set(target, SpriteAccessor.POSITION_XY).target(0, 0))
			.push(Tween.set(target, SpriteAccessor.SCALE_XY).target(10, 10))
			.push(Tween.set(target, SpriteAccessor.ROTATION).target(0))
			.pushPause(delay1)
			.beginParallel()
				.push(Tween.to(target, SpriteAccessor.OPACITY, 1000).target(1).ease(Quart.INOUT))
				.push(Tween.to(target, SpriteAccessor.SCALE_XY, 1000).target(1, 1).ease(Quart.INOUT))
			.end()
			.pushPause(-500)
			.push(Tween.to(target, SpriteAccessor.POSITION_XY, 1000).targetCurrent().ease(Back.OUT))
			.push(Tween.to(target, SpriteAccessor.ROTATION, 800).target(360).ease(Cubic.INOUT))
			.pushPause(delay2)
			.beginParallel()
				.push(Tween.to(target, SpriteAccessor.SCALE_XY, 300).target(3, 3).ease(Quad.IN))
				.push(Tween.to(target, SpriteAccessor.OPACITY, 300).target(0).ease(Quad.IN))
			.end()
			.start(tweenManager);
	}

	private void launchAnimation() {
		final int repeatCnt = 2;
		Tween startMark = Tween.mark();
		Tween endMark = Tween.mark();
		iterationCnt = 0;

		// The animation itself

		Timeline.createSequence()
			.push(startMark)
			.beginParallel()
				.push(buildAnimation(sprite1, 0, 1400))
				.push(buildAnimation(sprite2, 200, 1000))
				.push(buildAnimation(sprite3, 400, 600))
				.push(buildAnimation(sprite4, 600, 200))
			.end()
			.push(endMark)
			.repeat(repeatCnt, 0)
			.start(tweenManager);

		// The mark callbacks (to change the text at the right moments)

		/*startMark.addCallback(TweenCallback.Types.START, new TweenCallback() {
			@Override public void tweenEventOccured(Types eventType, Tween tween) {
				text = "Iteration: " + (++iterationCnt) + " / " + (repeatCnt+1);
			}
		});

		endMark.addBackwardsStartCallback(new TweenCallback() {
			@Override public void tweenEventOccured(Types eventType, Tween tween) {
				text = "Iteration: " + (--iterationCnt) + " / " + (repeatCnt+1);
			}
		});

		endMark.addStartCallback(new TweenCallback() {
			@Override public void tweenEventOccured(Types eventType, Tween tween) {
				if (iterationCnt >= 3) {
					text = "Forwards play complete (click to restart)";
					canBeRestarted = true;
				}
			}
		});

		startMark.addBackwardsStartCallback(new TweenCallback() {
			@Override public void tweenEventOccured(Types eventType, Tween tween) {
				if (iterationCnt <= 1) {
					text = "Backwards play complete (click to restart)";
					canBeRestarted = true;
				}
			}
		});*/
	}

	// -------------------------------------------------------------------------
	// INPUT
	// -------------------------------------------------------------------------

	private InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			if (canBeRestarted) {
				canBeRestarted = false;
				// If the user touches the screen, we kill every running tween
				// and restart the animation.
				tweenManager.killAll();
				launchAnimation();
			}
			return true;
		}

		@Override
		public boolean touchMoved(int x, int y) {
			if (canControlSpeed) {
				int w = Gdx.graphics.getWidth();
				speed = 4f * (x-w/2)/w;
			}
			return true;
		}
	};
}
