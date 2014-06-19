package singlefinallite.system;

import java.io.*;

import java.text.*;

import java.util.*;

/**
 * A class to provide a Properties Service through static
 * methods based on a key. This class MUST reside in a
 * package in order to have its methods statically importable!
 *
 * @author Rick Neff
 */
public class PropertiesService
{
   /**
    * The properties file extension.
    */
   public static String cPropertiesExtension = ".props";

   /**
    * Don't let any instances be constructed.
    */
   private PropertiesService()
   {
   }

   /**
    * The map of String name to Properties objects.
    */
   private static Map<String, Properties> cMap;

   static
   {
      cMap = new HashMap<String, Properties>();

      cMap.put("system", System.getProperties());
   }

   /**
    * Gets a Properties object by a String key.
    *
    * @param pKey the key.
    *
    * @return the Properties object.
    */
   public static Properties get(String pKey)
   {
      Properties props = cMap.get(pKey);

      if (props == null)
      {
         props = load(pKey);
         cMap.put(pKey, props);
      }

      return props;
   }

   /**
    * Loads a Properties object by a String key.
    *
    * @param pKey the key.
    *
    * @return the Properties object.
    */
   private static Properties load(String pKey)
   {
      Properties loaded = new Properties();

      try
      {
         String key = pKey + cPropertiesExtension;
         loaded.load(ResourceGetter.getResourceAsStream(key));
      }
      catch (Exception e)
      {
         System.err.println(e);
      }

      return loaded;
   }

   /**
    * Gets the file from/to which a Properties object is loaded/stored.
    *
    * @param pKey the key.
    *
    * @return the File.
    */
   private static File getFile(String pKey)
   {
      File file = new File(pKey + cPropertiesExtension);
      return file;
   }

   /**
    * Puts (stores) a Properties object keyed by a String.
    *
    * @param pKey the key.
    */
   public static void put(String pKey)
   {
      Properties props = get(pKey);
      String comments =
         "Stored: " +
         DateFormat.getDateInstance(DateFormat.LONG).format(new Date());

      try
      {
         File file = getFile(pKey);
         File parentFile = file.getParentFile();
         if (parentFile != null)
         {
            new Shell().mkdir(parentFile);
         }

         OutputStream out = new FileOutputStream(file);
         props.store(out, comments);
      }
      catch (Exception e)
      {
         System.out.println(e);
      }
   }

   public static void putToFile(String pFileName, String pKey, String pValue)
   {
        Properties props = new Properties();
        props.put(pKey, pValue);
        putToFile(pFileName, props);
   }

   public static void putToFile(String pFileName, Properties properties)
   {
      try
      {
        FileOutputStream fos = new FileOutputStream(
           ResourceGetter.cResourcesPathPrefix.substring(1) +
           pFileName + cPropertiesExtension);
        
        properties.store(fos, "Stored: " + 
          DateFormat.getDateInstance(DateFormat.LONG).format(new Date()));
        fos.flush();
        fos.close();  
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
   }

   public static void addProperty(Properties property, String pKey, String pValue)
   {
      property.put(pKey, pValue);
   }

   /**
    * Gets all Properties keys.
    *
    * @return all Properties keys.
    */
   public static List<String> getAllPropertiesKeys()
   {
      List<String> list = new ArrayList<String>();
      list.addAll(cMap.keySet());

      return list;
   }

   /**
    * Sets the string value of this context-named property setting.
    *
    * @param pObject the object naming the context
    *        whose property value is being set.
    * @param pKey the name of the property.
    * @param pValue the value to set.
    */
   public static void setString(final Object pObject, final String pKey,
      final String pValue)
   {
      setString(pObject.getClass(), pKey, pValue);
   }

   /**
    * Sets the string value of this context-named property setting.
    *
    * @param pClass the class object naming the context
    *        whose property value is being set.
    * @param pKey the name of the property.
    * @param pValue the value to set.
    */
   public static void setString(final Class pClass, final String pKey,
      final String pValue)
   {
      setString(pClass.getName(), 0, pKey, pValue);
   }

   /**
    * Sets the string value of this context-named property setting.
    *
    * @param pClassName the class name of the context
    *        whose property value is being set.
    * @param pDummy a dummy parameter to help the compiler disambiguate.
    * @param pKey the name of the property.
    * @param pValue the value to set.
    */
   public static void setString(final String pClassName, final int pDummy,
      final String pKey, final String pValue)
   {
      if (! pValue.equals(getString(pClassName, pKey, "")))
      {
         get(pClassName).setProperty(pKey, pValue);
      }
   }

   /**
    * Sets the integer value of this context-named property setting.
    *
    * @param pObject the object whose property value is being set.
    * @param pKey the name of the property.
    * @param pValue the value to set.
    */
   public static void setInteger(final Object pObject, final String pKey,
      final int pValue)
   {
      setInteger(pObject.getClass(), pKey, pValue);
   }

   /**
    * Sets the integer value of this context-named property setting.
    *
    * @param pClass the class object naming the context
    *        whose property value is being set.
    * @param pKey the name of the property.
    * @param pValue the value to set.
    */
   public static void setInteger(final Class pClass, final String pKey,
      final int pValue)
   {
      setInteger(pClass.getName(), 0, pKey, pValue);
   }

   /**
    * Sets the integer value of this context-named property setting.
    *
    * @param pClassName the class name of the context
    *        whose property value is being set.
    * @param pDummy a dummy parameter to help the compiler disambiguate.
    * @param pKey the name of the property.
    * @param pValue the value to set.
    */
   public static void setInteger(final String pClassName, final int pDummy,
      final String pKey, final int pValue)
   {
      if (pValue == getInteger(pClassName, pKey, Integer.MIN_VALUE))
      {
         get(pClassName).setProperty(pKey, Integer.toString(pValue));
      }
   }

   /**
    * Sets the boolean value of this context-named property setting.
    *
    * @param pObject the object whose property value is being set.
    * @param pKey the name of the property.
    * @param pValue the value to set.
    */
   public static void setBoolean(final Object pObject, final String pKey,
      final boolean pValue)
   {
      setBoolean(pObject.getClass(), pKey, pValue);
   }

   /**
    * Sets the boolean value of this context-named property setting.
    *
    * @param pClass the class object naming the context
    *        whose property value is being set.
    * @param pKey the name of the property.
    * @param pValue the value to set.
    */
   public static void setBoolean(final Class pClass, final String pKey,
      final boolean pValue)
   {
      setBoolean(pClass.getName(), 0, pKey, pValue);
   }

   /**
    * Sets the boolean value of this context-named property setting.
    *
    * @param pClassName the class name of the context
    *        whose property value is being set.
    * @param pDummy a dummy parameter to help the compiler disambiguate.
    * @param pKey the name of the property.
    * @param pValue the value to set.
    */
   public static void setBoolean(final String pClassName, final int pDummy,
      final String pKey, final boolean pValue)
   {
      get(pClassName).setProperty(pKey, Boolean.toString(pValue));
   }

   /**
    * Gets the string value of this context-named property setting.
    *
    * @param pObject the object whose property value is sought.
    * @param pKey the name of the property
    * @param pDefaultValue the default value if none is found.
    *
    * @return the string value of this context-named property setting.
    */
   public static String getString(final Object pObject, final String pKey,
      final String pDefaultValue)
   {
      return getString(pObject.getClass().getName(), 0, pKey, pDefaultValue);
   }

   /**
    * Gets the string value of this context-named property setting.
    *
    * @param pClass the class object whose property value is sought.
    * @param pKey the name of the property
    * @param pDefaultValue the default value if none is found.
    *
    * @return the string value of this context-named property setting.
    */
   public static String getString(final Class pClass, final String pKey,
      final String pDefaultValue)
   {
      return getString(pClass.getName(), 0, pKey, pDefaultValue);
   }

   /**
    * Gets the string value of this context-named property setting.
    *
    * @param pClassName the name of the class whose property value is sought.
    * @param pDummy an int dummy variable to disambiguate which call is made.
    * @param pKey the name of the property
    * @param pDefaultValue the default value if none is found.
    *
    * @return the string value of this context-named property setting.
    */
   public static String getString(final String pClassName, final int pDummy,
      final String pKey, final String pDefaultValue)
   {
      return get(pClassName).getProperty(pKey, pDefaultValue);
   }

   /**
    * @param pObject the object whose property value is sought.
    * @param pKey the name of the property
    * @param pDefaultValue the default value if none is found.
    *
    * @return the integer value of this context-named property setting.
    */
   public static Integer getInteger(final Object pObject, final String pKey,
      final Integer pDefaultValue)
   {
      return getInteger(pObject.getClass().getName(), 0, pKey, pDefaultValue);
   }

   /**
    * Gets the integer value of this context-named property setting.
    *
    * @param pClass the class object whose property value is sought.
    * @param pKey the name of the property
    * @param pDefaultValue the default value if none is found.
    *
    * @return the integer value of this context-named property setting.
    */
   public static Integer getInteger(final Class pClass, final String pKey,
      final Integer pDefaultValue)
   {
      return getInteger(pClass.getName(), 0, pKey, pDefaultValue);
   }

   /**
    * Gets the integer value of this context-named property setting.
    *
    * @param pClassName the name of the class whose property value is sought.
    * @param pDummy an int dummy variable to disambiguate which call is made.
    * @param pKey the name of the property
    * @param pDefaultValue the default value if none is found.
    *
    * @return the integer value of this context-named property setting.
    */
   public static Integer getInteger(final String pClassName, final int pDummy,
      final String pKey, final Integer pDefaultValue)
   {
      Integer rtnval = pDefaultValue;
      Properties named = get(pClassName);
      String value = named.getProperty(pKey);

      if (value != null)
      {
         try
         {
            rtnval = Integer.parseInt(value);
         }
         catch (Exception e)
         {
         }
      }

      return rtnval;
   }

   /**
    * Gets the boolean value of this context-named property setting.
    *
    * @param pObject the object whose property value is sought.
    * @param pKey the name of the property
    * @param pDefaultValue the default value if none is found.
    *
    * @return the boolean value of this context-named property setting.
    */
   public static Boolean getBoolean(final Object pObject, final String pKey,
      final Boolean pDefaultValue)
   {
      return getBoolean(pObject.getClass().getName(), 0, pKey, pDefaultValue);
   }

   /**
    * Gets the boolean value of this context-named property setting.
    *
    * @param pClass the class object whose property value is sought.
    * @param pKey the name of the property
    * @param pDefaultValue the default value if none is found.
    *
    * @return the boolean value of this context-named property setting.
    */
   public static Boolean getBoolean(final Class pClass, final String pKey,
      final Boolean pDefaultValue)
   {
      return getBoolean(pClass.getName(), 0, pKey, pDefaultValue);
   }

   /**
    * Gets the boolean value of this context-named property setting.
    *
    * @param pClassName the name of the class whose property value is sought.
    * @param pDummy an int dummy variable to disambiguate which call is made.
    * @param pKey the name of the property
    * @param pDefaultValue the default value if none is found.
    *
    * @return the boolean value of this context-named property setting.
    */
   public static Boolean getBoolean(final String pClassName, final int pDummy,
      final String pKey, final Boolean pDefaultValue)
   {
      String value = get(pClassName).getProperty(pKey);

      return ((value == null) ? pDefaultValue : value.equalsIgnoreCase("true"));
   }
}
