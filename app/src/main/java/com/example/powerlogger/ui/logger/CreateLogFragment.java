package com.example.powerlogger.ui.logger;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.powerlogger.MainActivityViewModel;
import com.example.powerlogger.R;
import com.example.powerlogger.databinding.FragmentCreateLogBindingImpl;
import com.example.powerlogger.dto.ExerciseDTO;
import com.example.powerlogger.dto.GroupDTO;
import com.example.powerlogger.model.ExerciseCategory;
import com.example.powerlogger.utils.ArrayUtills;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateLogFragment extends Fragment {
    private MainActivityViewModel mainActivityViewModel;
    public CreateOrEditLogViewModel createOrEditLogViewModel;

    private String currentDateInView;

    private FragmentCreateLogBindingImpl bindingFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle data = getArguments();

        try {
            currentDateInView = data.getString(LogConstants.CURRENT_DATE_BUNDLE_KEY);
        } catch (NullPointerException e) {
            currentDateInView = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        createOrEditLogViewModel = new ViewModelProvider(this).get(CreateOrEditLogViewModel.class);

        // Inflate the layout for this fragment
        bindingFragment = DataBindingUtil.inflate(inflater,
                R.layout.fragment_create_log, container, false);

        View view = bindingFragment.getRoot();
        bindingFragment.setModel(createOrEditLogViewModel);
        bindingFragment.setLifecycleOwner(this);

        bindingFragment.exerciseType.setOnConfirmConsumer(this::onSelectCategory);
        bindingFragment.exercisesSpinner.setOnConfirmConsumer(this::onSelectExercise);

        bindingFragment.addExerciseButton.setOnClickListener(this::onAddNewExercise);

        mainActivityViewModel.getExerciseLiveData().observe(getViewLifecycleOwner(), bindingFragment.exercisesSpinner::setItems);
        bindingFragment.exercisesSpinner.setTitle(getStringRes(R.string.pick_exercise_title));

        mainActivityViewModel.getGroupsLiveData().observe(getViewLifecycleOwner(), bindingFragment.exerciseGroup::setItems);

        bindingFragment.exerciseGroup.setOnConfirmConsumer(this::onSelectGroups);
        bindingFragment.exerciseGroup.setTitle(getStringRes(R.string.add_to_group_title));

        List<ExerciseCategory> exerciseCategories = Arrays.stream(ExerciseCategory.values())
                .collect(Collectors.toList());

        bindingFragment.exerciseType.setItems(exerciseCategories);
        bindingFragment.exerciseType.setTitle(getStringRes(R.string.exercise_type_title));

        handleUIForAddMode();

        return view;
    }

    private void handleError(Throwable t) {
        Toast.makeText(this.getActivity().getApplicationContext(), "Something wrong happened", Toast.LENGTH_LONG).show();
    }

    private void handleSuccess(Object o) {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void handleUIForAddMode() {
        bindingFragment.confirmAddLog.setOnClickListener(this::onAddLogConfirm);
    }

    private void onAddLogConfirm(View v) {
        createOrEditLogViewModel.getLogDTO().setCreatedAt(currentDateInView);

        if (createOrEditLogViewModel.isCreatingNewExercise()) {
            // create exercise here and get the response
            createOrEditLogViewModel.createExercise(exercise ->
                            createOrEditLogViewModel.addLog(exercise, this::handleSuccess),
                    this::handleError);
        } else {
            createOrEditLogViewModel.addLog(this::handleSuccess);
        }
    }

    public void onAddNewExercise(View v) {
        if (bindingFragment.addNewExerciseLayout.isExpanded()) {
            createOrEditLogViewModel.setCreatingNewExercise(false);
            bindingFragment.exercisesSpinner.setDisabled(false);

        } else {
            createOrEditLogViewModel.setCreatingNewExercise(true);
            bindingFragment.exercisesSpinner.setDisabled(true);
        }

        bindingFragment.addNewExerciseLayout.toggle();
    }

    public void onSelectGroups(Object groups) {
        List<GroupDTO> selectedGroups = (List<GroupDTO>) groups;

        createOrEditLogViewModel.getCreatingExercise().setGroupIds(
                selectedGroups.stream().map(GroupDTO::getId).collect(Collectors.toList()));

        selectedGroups.forEach(checkedGroup -> {

            Chip chip = new Chip(getContext());
            chip.setText(checkedGroup.getName());
            chip.setCloseIconVisible(true);

            chip.setOnCloseIconClickListener(v -> {
                int index = ArrayUtills.findIndexByPredicate(
                        mainActivityViewModel.getGroupsLiveData().getValue(),
                        checkedGroup::equalIds
                );

                bindingFragment.exerciseGroup.setSelectedAtIndex(index, false);

                createOrEditLogViewModel.getCreatingExercise()
                        .getGroups()
                        .removeIf(checkedGroup::equalIds);

                bindingFragment.chipGroup.removeView(v);
            });

            bindingFragment.chipGroup.addView(chip);
        });
    }

    public void onSelectCategory(Object category) {
        try {
            ExerciseCategory cat = (ExerciseCategory) category;
            createOrEditLogViewModel.getCreatingExercise().setCategory(cat);
        } catch (Exception ex) {
            Log.e("CATEGORY SELECT ERR:", ex.getMessage());
        }
    }

    public void onSelectExercise(Object exerciseDTO) {
        try {
            ExerciseDTO casted = (ExerciseDTO) exerciseDTO;
            createOrEditLogViewModel.setSelectedExercise(casted);
        } catch (Exception ex) {
            Log.e("EXERCISE SELECT ERR:", ex.getMessage());
        }
    }


    private String getStringRes(int res) {
        return getResources().getString(res);
    }
}
