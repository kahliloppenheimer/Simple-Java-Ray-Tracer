package me.kahlil.scene;

import me.kahlil.config.JavaStyle;
import me.kahlil.geometry.Vector;
import org.immutables.value.Value.Immutable;

/** A representation of a camera positioned at a certain point in the scene. */
@Immutable
@JavaStyle
public interface Camera3D {

  Vector getLocation();
}
