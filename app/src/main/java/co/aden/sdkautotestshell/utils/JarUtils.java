package co.aden.sdkautotestshell.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils {
    private static JarFile jarFile;
    private static URLClassLoader urlClassLoader;
    private final static HashMap<Class<?>, HashMap<Method,List<Class<?>>>> ApiMap = new HashMap<>();


    public JarUtils(){
        new JarUtils("");
    }

    @Deprecated
    public JarUtils(JarFile inPutJarFile){
        jarFile = inPutJarFile;
    }

    public JarUtils(File file){
        try {
            jarFile = new JarFile(file);
            urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JarUtils(String jarPath){
        try {
            jarFile = new JarFile(new File(jarPath));
            urlClassLoader = new URLClassLoader(new URL[]{new File(jarPath).toURI().toURL()});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取jar包中所有类的全限定名称
     * @return
     */

    public static List<String> getAllClassQualifiedName() throws Exception {

        List<String> jarList = new LinkedList<>();

        if("".equals(jarFile.getName()))
            throw new Exception("jarfile is null !");

        try {
            Enumeration<JarEntry> jarsFileOfJar = jarFile.entries();

            while(jarsFileOfJar.hasMoreElements()){
                String currentFile = jarsFileOfJar.nextElement().getName();
                if(StringUtils.endsWith(currentFile, ".class")) {
                    currentFile = StringUtils.replace(currentFile, ".class","");
                    currentFile = StringUtils.replace(currentFile, "/", ".");
                    System.out.println(currentFile);
                    jarList.add(currentFile);
                }
            }
            return jarList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jarList;
    }

    /**
     * 加载整个jar包中的类
     * @return 加载的整个jar包中类的list
     */
    public static List<Class<?>> loadAllClassOfJar(){
        List<Class<?>> list = new LinkedList<>();
        try {
            for (String className : getAllClassQualifiedName()) {
                list.add(urlClassLoader.loadClass(className));
            }
            return list;

        } catch (Exception e) {
            Log.e("jarUtils", "加载jar包下所有类失败");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 制定类的全限定名称加载类
     * @param classQualifiedName
     * @return
     */
    public static Class<?> loadClassForName(String classQualifiedName){
        Class<?> clazz;
        try {
            clazz = urlClassLoader.loadClass(classQualifiedName);
            return clazz;
        } catch (ClassNotFoundException e) {
            Log.e("jarUtils", "加载["+classQualifiedName+"]类失败");
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private static HashMap<Class<?>, HashMap<Method,List<Type>>> getApiMap(){
        List<Type> paramList = null;
        HashMap<Method, List<Type>> methodListHashMap = null;
        HashMap<Class<?>,HashMap<Method, List<Type>>> apiMap = new HashMap<>();

        try {
            List<Class<?>> classList = loadAllClassOfJar();
            for (Class<?> aClass : classList) {
                Method[] methods= aClass.getDeclaredMethods();
                methodListHashMap = new HashMap<>();
                for (Method method : methods) {
                    Type[] types = method.getGenericParameterTypes();
                    paramList = new LinkedList<>();
                    for (Type type : types) {
                        //System.out.println(type.getTypeName());
                        paramList.add(type);
                    }
                    methodListHashMap.put(method, paramList);
                }
                System.out.println(methodListHashMap+"*************************");
                apiMap.put(aClass, methodListHashMap);
            }
            return apiMap;
        } catch (Exception e) {
            Log.e("getApiMap", "解析jar包失败");
            e.printStackTrace();
        } finally {
        }
        return null;
    }


}
