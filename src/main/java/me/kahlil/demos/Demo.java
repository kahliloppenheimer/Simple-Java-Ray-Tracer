package me.kahlil.demos;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static me.kahlil.geometry.LinearTransformation.rotateAboutZAxis;
import static me.kahlil.geometry.LinearTransformation.scale;
import static me.kahlil.geometry.LinearTransformation.translate;
import static me.kahlil.geometry.Triangle.equilateralTriangle;
import static me.kahlil.scene.Cameras.STANDARD_CAMERA;
import static me.kahlil.scene.Materials.glossy;
import static me.kahlil.scene.Materials.shiny;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import me.kahlil.geometry.Plane;
import me.kahlil.geometry.Shape;
import me.kahlil.geometry.Sphere;
import me.kahlil.geometry.Vector;
import me.kahlil.graphics.MyCanvas3D;
import me.kahlil.graphics.PhongShading;
import me.kahlil.graphics.RandomAntiAliasingMethod;
import me.kahlil.graphics.RayTracer;
import me.kahlil.graphics.RayTracerCoordinator;
import me.kahlil.graphics.ReflectiveRayTracer;
import me.kahlil.graphics.SimpleAntiAliaser;
import me.kahlil.scene.Camera;
import me.kahlil.scene.ImmutableMaterial;
import me.kahlil.scene.ImmutablePointLight;
import me.kahlil.scene.ImmutableScene;
import me.kahlil.scene.PointLight;
import me.kahlil.scene.Raster;
import me.kahlil.scene.Scene;

/** A second demo of the ray tracer. */
public class Demo {

  private static final int IMAGE_SIZE = 400;
  private static final int NUM_ANTI_ALIASING_SAMPLES = 1;

  public static void main(String[] args) throws InterruptedException, ExecutionException {

    Raster raster = new Raster(IMAGE_SIZE, IMAGE_SIZE);

    ImmutableList<Shape> shapes =
        ImmutableList.of(
            new Sphere(glossy().setColor(RED).build()).transform(translate(2, 0, -7)),
            new Sphere(shiny().setColor(GREEN).build()).transform(translate(-4, 0, -10)),
            new Sphere(glossy().setColor(BLUE).build()).transform(translate(-2, 0, -15)),
            equilateralTriangle(shiny().setColor(CYAN).build()).transform(scale(3.0).then(translate(2, 4, -15))),
            equilateralTriangle(shiny().setColor(CYAN).build()).transform(scale(3.0).then(rotateAboutZAxis(90)).then(translate(2, 4, -15))),
            equilateralTriangle(shiny().setColor(CYAN).build()).transform(scale(3.0).then(rotateAboutZAxis(180)).then(translate(2, 4, -15))),
            equilateralTriangle(shiny().setColor(CYAN).build()).transform(scale(3.0).then(rotateAboutZAxis(270)).then(translate(2, 4, -15))),
            new Sphere(
                ImmutableMaterial.builder()
                    .setColor(BLACK)
                    .setReflectiveness(1.0)
                    .setHardness(1)
                    .setSpecularIntensity(0.0)
                    .build()).transform(translate(0, 0, -10)),
            new Sphere(shiny().setColor(new Color(1.0f, 0.0f, 1.0f)).build())
                .transform(translate(0, 2, 1)),
            new Plane(
                new Vector(0, -1, 0),
                new Vector(0, 1.0, 0.0),
                glossy().setColor(new Color(72, 136, 168)).build()));

    // Lights in scene
    List<PointLight> lights =
        ImmutableList.of(
            ImmutablePointLight.builder()
                .setLocation(new Vector(3, 3, 0))
                .setColor(new Color(115, 115, 115))
                .build(),
            ImmutablePointLight.builder()
                .setLocation(new Vector(-6, 5, 0))
                .setColor(new Color(200, 200, 200))
                .build());

    // Whole scene
    Scene scene =
        ImmutableScene.builder()
            .setShapes(shapes)
            .setLights(lights)
            .setBackgroundColor(new Color(.25f, .25f, .25f))
            .setAmbient(new Color((float) .15, (float) .15, (float) .15))
            .build();

    Camera camera = STANDARD_CAMERA;

    boolean shadowsEnabled = true;

    RayTracer rayTracer =
        new SimpleAntiAliaser(
            raster,
            camera,
            new ReflectiveRayTracer(
                new PhongShading(scene, camera, shadowsEnabled), scene, raster, camera, 16),
            new RandomAntiAliasingMethod(NUM_ANTI_ALIASING_SAMPLES));
    //    RayTracer rayTracer = new SimpleRayTracer(
    ////        new NoShading(),
    //        new PhongShading(scene, camera, false),
    //        scene,
    //        raster,
    //        camera);
    //    RayTracer rayTracer = new ReflectiveRayTracer(
    //        new PhongShading(scene, camera, shadowsEnabled),
    //        scene,
    //        raster,
    //        camera,
    //        4);

    RayTracerCoordinator rt = new RayTracerCoordinator(raster, camera, scene, rayTracer);

    long start = System.currentTimeMillis();
    Raster rendered = rt.render(shadowsEnabled);
    long end = System.currentTimeMillis();

    System.out.println("Rendering took " + (end - start) + " ms");

    start = System.currentTimeMillis();
    paintToJpeg("images/demo.png", rendered);
    //    paintToJFrame(rendered);
    end = System.currentTimeMillis();
    System.out.println("Painting took " + (end - start) + " ms");
  }

  private static void paintToJFrame(Raster rendered) throws InterruptedException {
    MyCanvas3D canvas = new MyCanvas3D(rendered.getWidthPx(), rendered.getHeightPx());
    SwingUtilities.invokeLater(canvas::createAndShowGUI);
    Thread.sleep(500);

    canvas.paintFrame(rendered);
    SwingUtilities.invokeLater(canvas::refresh);
  }

  private static void paintToJpeg(String fileName, Raster rendered) {
    int height = rendered.getHeightPx();
    int width = rendered.getWidthPx();
    BufferedImage theImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int value = rendered.getPixel(i, j).getRGB();
        theImage.setRGB(j, i, value);
      }
    }
    File outputFile = new File(fileName);
    try {
      for (int i = 0; outputFile.exists(); i++) {
        outputFile =
            new File(
                String.format("%s-%d.png", fileName.substring(0, fileName.indexOf(".png")), i));
      }
      outputFile.createNewFile();
      ImageIO.write(theImage, "png", outputFile);
      System.out.printf("Created %s\n", outputFile.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
