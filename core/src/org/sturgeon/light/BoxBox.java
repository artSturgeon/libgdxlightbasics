package org.sturgeon.light;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by henri on 24/01/2016.
 */
public class BoxBox extends ScreenAdapter {

    TextureRegion region;
    Rectangle feller;
    Body fellerBody;
    Body platformBody;

    TextureRegion platformRegion;

    World world;
    Box2DDebugRenderer debugRenderer;

    Box game;

    OrthographicCamera camera;

    RayHandler rayHandler;

    PointLight light;

    public BoxBox(final Box g) {
        game = g;

        camera = new OrthographicCamera(80, 48);
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0f);
        camera.update();


        region = new TextureRegion(new Texture(Gdx.files.internal("feller1.png")));
        feller = new Rectangle(40, 20, 3.2f, 3.2f);

        platformRegion = new TextureRegion(new Texture(Gdx.files.internal("platform1.png")));
        // Box2d bits
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();

        // light bits
        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 1f);
        //rayHandler.setShadows(false);

        createBody();
        createGround();
        createPlatform();
        createPointLight();
    }

    private void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(feller.x, feller.y);

        fellerBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        // setAsBox takes half height/width
        shape.setAsBox(feller.width/2, feller.height/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.3f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.6f;

        fellerBody.createFixture(fixtureDef);
        shape.dispose();
    }

    private void createPlatform() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(20, 10);

        platformBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(3.2f, 1.6f);
        platformBody.createFixture(shape, 0.0f);
        shape.dispose();
    }

    private void createGround() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(0, 1));
        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(camera.viewportWidth, 1.0f);
        groundBody.createFixture(groundBox, 0.0f);

        groundBox.dispose();
    }

    private void createPointLight() {
        light = new PointLight(rayHandler, 12, Color.CYAN, 20, 10, 0);
        light.attachToBody(fellerBody);
        //new PointLight(rayHandler, 12, Color.WHITE, 50, 40, 24);
        new ConeLight(rayHandler, 12, Color.WHITE, 50, 40, 24, 270, 45);
        //new PointLight(rayHandler, 12, Color.GREEN, 20, platformBody.getPosition().x, platformBody.getPosition().y);

    }

    @Override
    public void render (float delta) {

        camera.position.set(fellerBody.getPosition().x, fellerBody.getPosition().y, 0);
        camera.update();
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        game.batch.setProjectionMatrix(camera.combined);
        game.batch.disableBlending();

        Vector2 vel = fellerBody.getLinearVelocity();
        Vector2 pos = fellerBody.getPosition();
        Vector2 platPos = platformBody.getPosition();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && vel.x > -200) {
            fellerBody.applyLinearImpulse(-1f, 0,  pos.x, pos.y, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && vel.x < 200) {
            fellerBody.applyLinearImpulse(1f, 0,  pos.x, pos.y, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            feller.y += 200 * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            feller.y -= 200 * delta;
        }

        pos = fellerBody.getPosition();

        game.batch.begin();
       // game.batch.draw(fellerImage, pos.x, pos.y, 1f, 1f, 6.4f, 6.4f, 1f, 1f);

        game.batch.draw(region, pos.x - 1.6f, pos.y - 1.6f, 3.2f, 3.2f);
                /*
                ,1f, 1f,
                3.2f, 3.2f,
                1, 1,
                0);
                */
        game.batch.draw(platformRegion, platPos.x - 3.2f, platPos.y - 1.6f, 6.4f, 3.2f);
        game.batch.end();

        // for lights

        rayHandler.setCombinedMatrix(camera);
        rayHandler.update();
        rayHandler.render();


        world.step(1/60f, 6, 2);
        debugRenderer.render(world, camera.combined);
    }


}
