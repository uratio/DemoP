package com.uratio.demop.livedata;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

/**
 * @author lang
 * @data 2021/6/29
 */
public class MyViewModel extends ViewModel {
    private final PostalCodeRepository repository;
    private final MutableLiveData<String> addressInput = new MutableLiveData<>();
    private final LiveData<String> postalCode = Transformations.switchMap(addressInput, new Function<String, LiveData<String>>() {
        @Override
        public LiveData<String> apply(String s) {
            return getPostalCode(s);
        }
    });

    public MyViewModel(PostalCodeRepository repository) {
        this.repository = repository;
    }

    public LiveData<String> getPostalCode(String address) {
        return repository.getPostCode(address);
    }

    private void setInput(String address) {
        addressInput.setValue(address);
    }



    private MutableLiveData<List<User>> users;

    public LiveData<List<User>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<>();
            loadUsers();
        }
        return users;
    }

    private void loadUsers() {
        // 执行异步操作获取用户。
    }
}
