package org.openhab.binding.nest.internal.api.listeners;

public interface CompletionListener {

        void onComplete();
        void onError(int errorCode);
}
