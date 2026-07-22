importScripts("https://www.gstatic.com/firebasejs/12.16.0/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/12.16.0/firebase-messaging-compat.js");

firebase.initializeApp({
    apiKey: "AIzaSyBHS54lB4O1eAqU1qxEH1IU4l_0GRRBUAQ",
    authDomain: "triplog-75c71.firebaseapp.com",
    projectId: "triplog-75c71",
    storageBucket: "triplog-75c71.firebasestorage.app",
    messagingSenderId: "181905578934",
    appId: "1:181905578934:web:f2ecf5b0e2e035c2a43f17"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
    const notification = payload.notification || {};
    self.registration.showNotification(notification.title || "Triplog 알림", {
        body: notification.body || "새로운 알림이 도착했습니다.",
        icon: "/images/triplog-logo.png",
        data: payload.data || {}
    });
});
