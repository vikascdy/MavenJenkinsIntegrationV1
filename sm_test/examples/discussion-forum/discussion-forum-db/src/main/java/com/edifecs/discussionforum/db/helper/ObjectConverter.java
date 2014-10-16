package com.edifecs.discussionforum.db.helper;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.edifecs.discussionforum.api.model.Category;
import com.edifecs.discussionforum.api.model.Reply;
import com.edifecs.discussionforum.api.model.Topic;
import com.edifecs.discussionforum.jpa.entity.CategoryEntity;
import com.edifecs.discussionforum.jpa.entity.ReplyEntity;
import com.edifecs.discussionforum.jpa.entity.TopicEntity;


public class ObjectConverter {

	private static final Map<Class<? extends Serializable>, Class<? extends Object>> API_TO_JPA_CLASSES = new HashMap<Class<? extends Serializable>, Class<? extends Object>>();
	private static final Map<Class<? extends Object>, Class<? extends Serializable>> JPA_TO_API_CLASSES = new HashMap<Class<? extends Object>, Class<? extends Serializable>>();

	private static void conversion(Class<? extends Serializable> a,
			Class<? extends Object> b) {
		API_TO_JPA_CLASSES.put(a, b);
		JPA_TO_API_CLASSES.put(b, a);
	}

	static {
		conversion(Category.class, CategoryEntity.class);
        conversion(Topic.class, TopicEntity.class);
        conversion(Reply.class, ReplyEntity.class);
	}

	public static Object modelToEntity(Serializable apiObj) {
		if (!API_TO_JPA_CLASSES.containsKey(apiObj.getClass())) {
			throw new IllegalArgumentException("The class " + apiObj.getClass()
					+ " does not have a defined conversion.");
		}
		final Object jpaObj;
		try {
			jpaObj = API_TO_JPA_CLASSES.get(apiObj.getClass()).newInstance();
			// persist exisiting JPA obj properties

		} catch (InstantiationException ex) {
			throw new RuntimeException(ex.getCause());
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		copyCommonBeanProperties(apiObj, jpaObj);
		return jpaObj;
	}

	public static Serializable entityToModel(Object jpaObj) {
		if (!JPA_TO_API_CLASSES.containsKey(jpaObj.getClass())) {
			throw new IllegalArgumentException("The class " + jpaObj.getClass()
					+ " does not have a defined conversion.");
		}
		final Serializable apiObj;
		try {
			apiObj = JPA_TO_API_CLASSES.get(jpaObj.getClass()).newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex.getCause());
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		copyCommonBeanProperties(jpaObj, apiObj);


		return apiObj;
	}

	static void copyCommonBeanProperties(Object sourceBean, Object destBean) {
		final Map<String, Method> getters = new HashMap<String, Method>();
		for (Method m : sourceBean.getClass().getMethods()) {
			if (m.getName().startsWith("get")
					&& m.getParameterTypes().length == 0
					&& Modifier.isPublic(m.getModifiers())
					&& !Modifier.isStatic(m.getModifiers())) {
				getters.put(m.getName().substring(3), m);
			} else if (m.getName().startsWith("is")
					&& m.getParameterTypes().length == 0
					&& Modifier.isPublic(m.getModifiers())
					&& !Modifier.isStatic(m.getModifiers())
					&& m.getReturnType() == boolean.class) {
				getters.put(m.getName().substring(2), m);
			}
		}
		final Map<String, Method> setters = new HashMap<String, Method>();
		final Map<String, Method> destBeanGetters = new HashMap<String, Method>();
		for (Method m : destBean.getClass().getMethods()) {
			if (m.getName().startsWith("set")
					&& m.getParameterTypes().length == 1
					&& Modifier.isPublic(m.getModifiers())
					&& !Modifier.isStatic(m.getModifiers())) {
				setters.put(m.getName().substring(3), m);
			} else if (m.getName().startsWith("get")
					&& m.getParameterTypes().length == 0
					&& Modifier.isPublic(m.getModifiers())
					&& !Modifier.isStatic(m.getModifiers())) {
				destBeanGetters.put(m.getName().substring(3), m);
			}
		}

		for (Map.Entry<String, Method> e : getters.entrySet()) {
			if (setters.containsKey(e.getKey())) {
				final Method getter = e.getValue();
				final Method destBeanGetter = destBeanGetters.get(e.getKey());
				final Method setter = setters.get(e.getKey());
				final Class<?> paramType = setter.getParameterTypes()[0];
				Object toSet;
				try {
					toSet = getter.invoke(sourceBean);
				} catch (InvocationTargetException ex) {
					throw new RuntimeException(ex.getCause());
				} catch (IllegalAccessException ex) {
					throw new RuntimeException(ex);
				}

				if (toSet == null) {
					// Null can be safely assigned to anything...
					// if (paramType.isPrimitive()) {
					// // ...except, don't assign null to primitives.
					// continue;
					// }
					// Null can ovveride Id of persisited entity
					continue;
				} else if (!paramType.isAssignableFrom(toSet.getClass())) {
					if (paramType == String.class) {
						toSet = String.valueOf(toSet);
					} else if (paramType == long.class
							&& toSet instanceof Number) {
						toSet = ((Number) toSet).longValue();
					} else if (paramType == int.class
							&& toSet instanceof Number) {
						toSet = ((Number) toSet).intValue();
					} else if (paramType == long.class
							&& toSet instanceof String) {
						toSet = Long.parseLong(toSet.toString());
					} else if (paramType == int.class
							&& toSet instanceof String) {
						toSet = Integer.parseInt(toSet.toString());
					} else if (paramType == boolean.class
							&& toSet instanceof Boolean) {
						toSet = ((Boolean) toSet).booleanValue();
					} else if (API_TO_JPA_CLASSES.containsKey(toSet.getClass())
							&& paramType.isAssignableFrom(API_TO_JPA_CLASSES
									.get(toSet.getClass()))) {
						try {
							// check if dest setter is not null and has any
							// values which should not be
							// overriden like jpa relationships
							Object destBeanProperty = destBeanGetter
									.invoke(destBean);
							if (null != destBeanProperty) {
								copyCommonBeanProperties(toSet,
										destBeanProperty);
								continue;
							} else {
								toSet = modelToEntity((Serializable) toSet);
							}
						} catch (Exception e1) {
							throw new RuntimeException(e1);
						}
					} else if (JPA_TO_API_CLASSES.containsKey(toSet.getClass())
							&& paramType.isAssignableFrom(JPA_TO_API_CLASSES
									.get(toSet.getClass()))) {
						toSet = entityToModel((Object) toSet);
					} else {
						throw new UnsupportedOperationException(
								String.format(
										"The return type of"
												+ " %s.%s and the parameter type of %s.%s are incompatible (%s and %s)."
												+ " These objects cannot be converted.",
										sourceBean.getClass(),
										getter.getName(), destBean.getClass(),
										setter.getName(),
										getter.getReturnType(),
										setter.getParameterTypes()[0]));
					}
				} else if (toSet instanceof List<?>) {
					// Convert subitems of lists, if necessary.
					toSet = convertList((List<?>) toSet);
				}

				try {
					setter.invoke(destBean, toSet);
				} catch (InvocationTargetException ex) {
					throw new RuntimeException(ex.getCause());
				} catch (IllegalAccessException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}

	private static List<Object> convertList(Collection<?> orig) {
		final List<Object> ret = new ArrayList<Object>(orig.size());
		for (Object item : orig) {
			if (API_TO_JPA_CLASSES.containsKey(item.getClass())) {
				ret.add(modelToEntity((Serializable) item));
			} else if (JPA_TO_API_CLASSES.containsKey(item.getClass())) {
				ret.add(entityToModel((Object) item));
			} else {
				ret.add(item);
			}
		}
		return ret;
	}
}