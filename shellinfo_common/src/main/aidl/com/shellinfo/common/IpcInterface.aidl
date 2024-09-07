// IpcInterface.aidl
package com.shellinfo.common;
import com.shellinfo.common.BaseMessage;
// Declare any non-default types here with import statements

interface IpcInterface {
    BaseMessage getData();
    void sendData(int messageId,in BaseMessage message);
}