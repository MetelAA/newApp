package com.example.newapp.interfaces;

import java.util.ArrayList;

public interface CallbackInterfaceWithList {
    void requestResult(ArrayList list);
    void throwError(String error);
}
