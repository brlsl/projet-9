package com.openclassrooms.realestatemanager.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.realestatemanager.PropertyViewModel;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.activities.AddPropertyActivity;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.injection.ViewModelFactory;
import com.openclassrooms.realestatemanager.models.Property;
import com.openclassrooms.realestatemanager.utils.ItemClickSupport;
import com.openclassrooms.realestatemanager.views.PropertyList.PropertiesAdapter;

import java.util.ArrayList;
import java.util.List;


public class PropertiesListFragment extends BaseFragment {

    // FOR DATA
    private RecyclerView mRecyclerView;
    private PropertiesAdapter mAdapter;
    private List<Property> mPropertiesList = new ArrayList<>();
    private PropertyViewModel mPropertyViewModel;

    // FOR UI
    private FloatingActionButton mFabAddProperty;

    // CALLBACK
    private OnItemPropertyClickListener mCallback;

    public interface OnItemPropertyClickListener {
        void onItemPropertySelected(Property property);
    }

    // LIFECYCLE


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Now the Fragment needs to get an instance to the MainActivity that implements OnItemPropertyClickListener.
        try {
            mCallback = (OnItemPropertyClickListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " must implement SettingOptionsFragment.OnOptionClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rv_properties_list,container,false);

        configureRecyclerView(view);
        configureOnClickRecyclerView();

        configureViewModel();

        // observe Room database changes(add, update) and updates it in rv
        mPropertyViewModel.getPropertyList().observe(this, new Observer<List<Property>>() {
            @Override
            public void onChanged(List<Property> propertyList) {
                mAdapter.updatePropertyList(propertyList);
                mPropertiesList = propertyList;
            }
        });

        mFabAddProperty = view.findViewById(R.id.add_property_fab);
        mFabAddProperty.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddPropertyActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void configureRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.properties_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new PropertiesAdapter(mPropertiesList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView, R.layout.rv_property_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.e("TAG", "Position : "+position);
                        mCallback.onItemPropertySelected(mPropertiesList.get(position));
                    }
                });
    }

    private void configureViewModel() {
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(requireContext());
        mPropertyViewModel = new ViewModelProvider(this, mViewModelFactory).get(PropertyViewModel.class);
        //mPropertyViewModel.init();
    }

}
