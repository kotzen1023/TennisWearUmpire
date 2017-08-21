package com.seventhmoon.tenniswearumpire.Data;



public class Constants {
    public interface ACTION {
        String GET_STEP_COUNT_ACTION = "com.seventhmoon.tenniswearumpire.GetStepCountAction";

        String PLAY_MULTIFILES_COMPLETE = "com.seventhmoon.tenniswearumpire.PlayMultiFilesComplete";
    }

    public enum STATE {
        Created,
        Idle,
        Initialized,
        Preparing,
        Prepared,
        Started,
        Paused,
        Stopped,
        PlaybackCompleted,
        End,
        Error,

    }

    public enum VOICE_TYPE {
        GBR_MAN,
        GBR_WOMAN,
        USER_RECORD,
    }
}
