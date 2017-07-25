package nl.bluevoid.genpro.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public class VMUtil {

  public static boolean isVMRunningInServerMode() {
    String jitCompiler = getJitCompilerName();
    Debug.println("JITCompiler: " + jitCompiler);
    List<String> args = getVMarguments();
    for (String string : args) {
      Debug.println("VMarg: " + string);
    }
    return jitCompiler.toLowerCase().contains("server");

    // TODO FAILS!!! use JMX bean, but which?

    // if (string.equals("-server")) {
    // return true;
    // MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    // ObjectName runtime = new ObjectName("java.lang:type=RunTime");
    // mbeanserver.query(runtime, "foo");

    // }
    // }
  }

  public static String getJitCompilerName() {
    return ManagementFactory.getCompilationMXBean().getName();
  }

  public static List<String> getVMarguments() {
    RuntimeMXBean rmb = ManagementFactory.getRuntimeMXBean();
    return rmb.getInputArguments();
  }

  public static void main(String[] strings) {
    boolean b = isVMRunningInServerMode();
    Debug.print("isVMRunningInServerMode:" + b);
  }
}