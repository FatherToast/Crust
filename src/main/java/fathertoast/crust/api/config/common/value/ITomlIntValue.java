package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.file.TomlHelper;

import java.util.function.Supplier;

/**
 * A TOML value intended to wrap an {@link Integer} type to support prettier printing for
 * {@link TomlHelper#fieldInfoRange(ITomlIntValue, ITomlIntValue, ITomlIntValue)}
 * and config file values.
 */
public interface ITomlIntValue extends ITomlValue, Supplier<Integer> { }