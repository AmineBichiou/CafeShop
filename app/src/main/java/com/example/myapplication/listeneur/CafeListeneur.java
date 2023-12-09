package com.example.myapplication.listeneur;

import com.example.myapplication.Module.CafeModule;

import java.util.List;

public interface CafeListeneur {
    void onCafeLoadSuccess(List<CafeModule> cafeModuleList);
    void onCafeLoadFailed(String message);
}
