package me.kahlil.config;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A collection of {@link ThreadLocal} counters used to count interesting things during the ray
 * tracing computation.
 */
public final class Counters {

  /**
   * Counter of number of primary rays cast into the scene (e.g. one per pixel).
   */
  public static final AtomicLong NUM_PRIMARY_RAYS = new AtomicLong();

  /**
   * Counter of number of triangles included in the scene.
   */
  public static final AtomicLong NUM_TRIANGLES = new AtomicLong();

  /**
   * Counter of total number of rays traced during the ray tracing algorithm.
   */
  public static final AtomicLong NUM_TOTAL_RAYS = new AtomicLong();

  /**
   * Counter of total number of ray-shape intersection tests computed during the ray tracing algorithm.
   */
  public static final AtomicLong NUM_INTERSECTION_TESTS = new AtomicLong();

  /**
   * Counter of total number of actual ray-shape intersections found during the ray tracing algorithm.
   */
  public static final AtomicLong NUM_INTERSECTIONS = new AtomicLong();

  /**
   * Counter of total number of bounding volume intersection tests.
   */
  public static final AtomicLong NUM_BOUNDING_INTERSECTION_TESTS = new AtomicLong();

  /**
   * Counter of total number of bounding volume intersections.
   */
  public static final AtomicLong NUM_BOUNDING_INTERSECTIONS = new AtomicLong();

  /**
   * Counter of total number of ray-triangle tests computed during the ray tracing algorithm.
   */
  public static final AtomicLong NUM_TRIANGLE_TESTS = new AtomicLong();

  /**
   * Counter of total number of ray-triangle intersections computed during the ray tracing algorithm.
   */
  public static final AtomicLong NUM_TRIANGLE_INTERSECTIONS = new AtomicLong();

  /**
   * Counter of the total number of polygons stored in the octree that overlap between cells.
   */
  public static final AtomicLong NUM_OCTREE_INTERNAL_INSERTIONS = new AtomicLong();

  /**
   * Counter of the total number of polygons stored in the leaves of the Octree.
   */
  public static final AtomicLong NUM_OCTREE_CHILD_INSERTIONS = new AtomicLong();
}
