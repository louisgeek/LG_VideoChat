package com.louisgeek.lg_videochat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.louisgeek.chat.video.OnVideoChatListener;
import com.louisgeek.chat.video.VideoChatFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatDialogFragment newInstance(String param1, String param2) {
        ChatDialogFragment fragment = new ChatDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootLayout = inflater.inflate(R.layout.fragment_chat_dialog, container, false);
        initView(rootLayout);
        return rootLayout;
    }

    private VideoChatFragment mVideoChatFragment;
    private OnVideoChatListener mOnVideoChatListener = new OnVideoChatListener() {
        @Override
        public void doVideoChatInvite() {

        }

        @Override
        public void doVideoChatCancel(boolean isTimeout) {
            Toast.makeText(requireContext(), "取消", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void doVideoChatAgree() {
            Toast.makeText(requireContext(), "同意", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void doVideoChatReject() {
            Toast.makeText(requireContext(), "拒绝", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void doVideoChatEnd() {
            Toast.makeText(requireContext(), "挂断", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void doSwitchAudioVideo(boolean isVideo) {
            Toast.makeText(requireContext(), "切换到视频" + isVideo, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVideoChatInvite() {

        }

        @Override
        public void onVideoChatCancel(boolean isTimeout) {
            Toast.makeText(requireContext(), "被取消", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void onVideoChatOffer() {

        }

        @Override
        public void onVideoChatAgree() {
            Toast.makeText(requireContext(), "被同意", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVideoChatReject() {
            Toast.makeText(requireContext(), "被拒绝", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void onVideoChatAnswer() {

        }

        @Override
        public void onVideoChatEnd() {
            Toast.makeText(requireContext(), "被挂断", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void onSwitchAudioVideo(boolean isVideo) {
            Toast.makeText(requireContext(), "被切换到视频 " + isVideo, Toast.LENGTH_SHORT).show();
        }
    };

    private void initView(View rootLayout) {
        Button id_agree = rootLayout.findViewById(R.id.id_agree);
        Button id_reject = rootLayout.findViewById(R.id.id_reject);
        Button id_cancel = rootLayout.findViewById(R.id.id_cancel);
        Button id_end = rootLayout.findViewById(R.id.id_end);
        Button id_sw_av = rootLayout.findViewById(R.id.id_sw_av);
        id_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoChatFragment.doVideoChatAgree();
            }
        });
        id_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoChatFragment.doVideoChatReject();
            }
        });
        id_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoChatFragment.doVideoChatCancel(false);
            }
        });
        id_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoChatFragment.doVideoChatEnd();
            }
        });
        id_sw_av.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVideo = false;//todo
                mVideoChatFragment.doSwitchAudioVideo(isVideo);
            }
        });
        //
        String chatInfoModelJson = mParam1;
        mVideoChatFragment = VideoChatFragment.newInstance(chatInfoModelJson, "");
        mVideoChatFragment.addOnVideoChatListener(mOnVideoChatListener);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.id_frame_layout_container, mVideoChatFragment)
                .commitAllowingStateLoss();

    }


}