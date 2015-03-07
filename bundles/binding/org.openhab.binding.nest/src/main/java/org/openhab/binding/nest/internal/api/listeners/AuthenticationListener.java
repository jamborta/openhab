package org.openhab.binding.nest.internal.api.listeners;
public interface AuthenticationListener {
    void onAuthenticationSuccess();
    void onAuthenticationFailure(int errorCode);
}