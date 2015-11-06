package org.openhab.binding.nestfirebase.internal.api.listeners;

public interface CompletionListener {

        void onComplete();
        void onError(int errorCode);
}
