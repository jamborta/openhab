package org.openhab.binding.nestfirebase.internal.api.listeners;
public interface AuthenticationListener {
    void onAuthenticationSuccess();
    void onAuthenticationFailure(int errorCode);
}