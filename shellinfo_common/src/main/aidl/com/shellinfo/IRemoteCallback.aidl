package com.shellinfo;

interface IRemoteCallback {
    void onMessageReceived(int messageId,String message);
}