package com.example.newapp.interfaces;

import com.example.newapp.core.LessonForScheduleSettings;

import java.util.ArrayList;

public interface CallbackInterfaceWithList {
    void requestResult(ArrayList list);
    void throwError(String error);
}
