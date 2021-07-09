package com.uratio.demop.livedata;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * @author lang
 * @data 2021/6/28
 */
public class NameViewModel extends ViewModel {

    // Create a LiveData with a String
    private MutableLiveData<String> currentName;

    public MutableLiveData<String> getCurrentName() {
        if (currentName == null) {
            currentName = new MutableLiveData<String>();
        }
        return currentName;
    }

// Rest of the ViewModel...
}
