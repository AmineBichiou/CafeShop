package com.example.myapplication.listeneur;

import com.example.myapplication.Module.CafeModule;
import com.example.myapplication.Module.CartModule;

import java.util.List;

public interface CartListeneur {
    void onCartLoadSuccess(List<CartModule> cartModuleList);
    void onCartLoadFailed(String message);
}
