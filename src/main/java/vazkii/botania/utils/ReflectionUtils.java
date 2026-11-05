package vazkii.botania.utils;

import java.lang.reflect.Field;

public final class ReflectionUtils {

	private ReflectionUtils() {}

	public static Field findField(Class<?> clazz, String... names) {
		for(String name : names) {
			try {
				Field field = clazz.getDeclaredField(name);
				field.setAccessible(true);
				return field;
			} catch(NoSuchFieldException e) {
				// Try the next candidate name
			}
		}
		return null;
	}

	public static Field findField(Class<?> clazz, int index) {
		try {
			Field field = clazz.getDeclaredFields()[index];
			field.setAccessible(true);
			return field;
		} catch(Exception e) {
			return null;
		}
	}

	public static int getInt(Field field, Object instance) {
		return getInt(field, instance, 0);
	}

	public static int getInt(Field field, Object instance, int def) {
		try {
			return field.getInt(instance);
		} catch(Exception e) {
			return def;
		}
	}

	public static boolean getBoolean(Field field, Object instance) {
		return getBoolean(field, instance, false);
	}

	public static boolean getBoolean(Field field, Object instance, boolean def) {
		try {
			return field.getBoolean(instance);
		} catch(Exception e) {
			return def;
		}
	}

	public static Object getObject(Field field, Object instance) {
		try {
			return field.get(instance);
		} catch(Exception e) {
			return null;
		}
	}

	public static void setInt(Field field, Object instance, int value) {
		try {
			field.setInt(instance, value);
		} catch(Exception e) {
		}
	}

	public static void setBoolean(Field field, Object instance, boolean value) {
		try {
			field.setBoolean(instance, value);
		} catch(Exception e) {
		}
	}

	public static void setObject(Field field, Object instance, Object value) {
		try {
			field.set(instance, value);
		} catch(Exception e) {
		}
	}

}