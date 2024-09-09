// IRemoteService.aidl
package com.shellinfo;

import com.shellinfo.IRemoteCallback;
// Declare any non-default types here with import statements

interface IRemoteService {

    int sendData(int messageId, String message);

    void registerCallback(IRemoteCallback callback);
}