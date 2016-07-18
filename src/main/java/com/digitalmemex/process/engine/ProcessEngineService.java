package com.digitalmemex.process.engine;

import java.io.InputStream;
import java.util.List;

public interface ProcessEngineService {

    ProcessInfo deploy(String name, String fileName, InputStream deployStream);

    EngineInfo getEngineName();

    ProcessInfo getProcessInfo(String name);

    List<String> getProcessNameList();

    InstanceInfo startProcess(String key);

}
