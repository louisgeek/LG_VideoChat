package com.louisgeek.lg_videochat.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.louisgeek.chat.listener.OnChatListener;
import com.louisgeek.chat.model.ChatModel;
import com.louisgeek.lg_videochat.ChatFragment;
import com.louisgeek.lg_videochat.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatDialogFragment extends DialogFragment {
    private ChatFragment mChatFragment;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final OnChatListener mOnChatListener = new OnChatListener() {


        @Override
        public void doChatInvite(ChatModel chatModel) {

        }

        @Override
        public void doChatCancel(boolean isTimeout) {
            Toast.makeText(requireContext(), "取消", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void doChatAgree(ChatModel chatModel) {
            Toast.makeText(requireContext(), "同意", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void doChatReject() {
            Toast.makeText(requireContext(), "拒绝", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void doChatEnd() {
            Toast.makeText(requireContext(), "挂断", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void doSwitchAudioVideo(boolean isVideo) {
            Toast.makeText(requireContext(), "切换到视频" + isVideo, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onChatInvite(ChatModel chatModel) {

        }


        @Override
        public void onChatCancel(boolean isTimeout) {
            Toast.makeText(requireContext(), "被取消", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void onChatOffer() {

        }

        @Override
        public void onChatAgree(ChatModel chatModel) {
            Toast.makeText(requireContext(), "被同意", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onChatReject() {
            Toast.makeText(requireContext(), "被拒绝", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void onChatAnswer() {

        }

        @Override
        public void onChatEnd() {
            Toast.makeText(requireContext(), "被挂断", Toast.LENGTH_SHORT).show();
            dismissAllowingStateLoss();
        }

        @Override
        public void onSwitchAudioVideo(boolean isVideo) {
            Toast.makeText(requireContext(), "被切换到视频 " + isVideo, Toast.LENGTH_SHORT).show();
        }
    };
    private String mParam2;

    public ChatDialogFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types of parameters
    private ChatModel mChatModel;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatDialogFragment newInstance(ChatModel chatModel, String param2) {
        ChatDialogFragment fragment = new ChatDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, chatModel);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootLayout = inflater.inflate(R.layout.fragment_chat_dialog, container, false);
        initView(rootLayout);
        return rootLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChatModel = (ChatModel) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void initView(View rootLayout) {
        Button id_agree = rootLayout.findViewById(R.id.id_agree);
        Button id_reject = rootLayout.findViewById(R.id.id_reject);
        Button id_cancel = rootLayout.findViewById(R.id.id_cancel);
        Button id_end = rootLayout.findViewById(R.id.id_end);
        Button id_sw_av = rootLayout.findViewById(R.id.id_sw_av);
        id_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatFragment.doChatAgree();
            }
        });
        id_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatFragment.doChatReject();
            }
        });
        id_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatFragment.doChatCancel(false);
            }
        });
        id_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatFragment.doChatEnd();
            }
        });
        id_sw_av.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVideo = false;//todo
                mChatFragment.doSwitchAudioVideo(isVideo);
            }
        });
        //
        //直接打开
        mChatFragment = ChatFragment.newInstance(mChatModel, "");
        mChatFragment.addOnChatListener(mOnChatListener);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.id_frame_layout_container, mChatFragment)
                .commitAllowingStateLoss();

    }


}