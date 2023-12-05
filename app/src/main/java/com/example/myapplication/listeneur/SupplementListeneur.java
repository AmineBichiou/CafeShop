package com.example.myapplication.listeneur;

import com.example.myapplication.Module.CafeModule;
import com.example.myapplication.Module.SupplementModule;

import java.util.List;

public interface SupplementListeneur {
    void onSuppLoadSuccess(List<SupplementModule> supplementModuleList);
    void onSuppLoadFailed(String message);
}
