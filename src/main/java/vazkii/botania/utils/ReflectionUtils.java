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

	public static int getInt(Field field, Object instance) {
		try {
			return field.getInt(instance);
		} catch(Exception e) {
			return 0;
		}
	}

	public static Object getObject(Field field, Object instance) {
		try {
			return field.get(instance);
		} catch(Exception e) {
			return null;
		}
	}


}