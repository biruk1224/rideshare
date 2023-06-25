package com.bgmnt.rideshare.ui.home;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bgmnt.rideshare.CreatorActivity;
import com.bgmnt.rideshare.JoinerActivity;
import com.bgmnt.rideshare.Preference_Manager;
import com.bgmnt.rideshare.R;
import com.bgmnt.rideshare.databinding.FragmentHomeBinding;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Preference_Manager preference_manager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        preference_manager = new Preference_Manager(requireActivity());
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        this.binding.createa.setOnClickListener(view -> HomeFragment.this.openCreate());

        this.binding.joina.setOnClickListener(view -> HomeFragment.this.openJoin());



        if (!Openedbefore()) {

            new TapTargetSequence(requireActivity()).targets(TapTarget.forView(binding.createa, "Create Group", "This means you will be visible for people who want to share a transporation fee").outerCircleColor(R.color.rideshare).outerCircleAlpha(0.95f).textColor(R.color.white).titleTextSize(18).descriptionTextSize(15).descriptionTextColor(R.color.white).textTypeface(Typeface.SERIF).dimColor(R.color.black).drawShadow(true).cancelable(false).tintTarget(true).transparentTarget(true).targetRadius(70), TapTarget.forView(this.binding.joina, "Join A Group", "This button will show you people within a 3km radius from your location who want to share a transportation fee.").outerCircleColor(R.color.white).outerCircleColor(R.color.rideshare).outerCircleAlpha(0.95f).textColor(R.color.white).titleTextSize(18).descriptionTextSize(15).descriptionTextColor(R.color.white).textTypeface(Typeface.SERIF).dimColor(R.color.black).drawShadow(true).cancelable(false).tintTarget(true).targetRadius(70).transparentTarget(true)).listener(new TapTargetSequence.Listener() {

                @Override
                public void onSequenceCanceled(TapTarget tapTarget) {
                }

                @Override
                public void onSequenceFinish() {
                    HomeFragment.this.preference_manager.putBoolean("Walk through", true);
                }

                @Override
                public void onSequenceStep(TapTarget tapTarget, boolean z) {
                    HomeFragment.this.binding.scroll.smoothScrollTo(0, HomeFragment.this.binding.scroll.getBottom());
                }
            }).start();
        }


        binding.CREATE.setOnClickListener(v -> startActivity(new Intent(getActivity(), CreatorActivity.class)));
        binding.JOIN.setOnClickListener(v -> startActivity(new Intent(getActivity(), JoinerActivity.class)));
        return root;
    }
    public  void openCreate() {
        startActivity(new Intent(getActivity(), CreatorActivity.class));
    }

    public  void openJoin() {
        startActivity(new Intent(getActivity(), JoinerActivity.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private boolean Openedbefore() {
        return preference_manager.getBoolean("Walk through");
    }
}