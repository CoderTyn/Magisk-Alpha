#pragma once

#include <pthread.h>
#include <string_view>
#include <functional>
#include <map>

#include <daemon.hpp>

#define SIGTERMTHRD SIGUSR1
#define ISOLATED_MAGIC "isolated"

// CLI entries
int enable_hide(bool late_props);
int disable_hide();
int add_list(int client);
int rm_list(int client);
void ls_list(int client);

// Process monitoring
extern pthread_t monitor_thread;
[[noreturn]] void proc_monitor();

// Utility functions
void crawl_procfs(const std::function<bool(int)> &fn);
bool hide_enabled();
void rebuild_uid_map();
bool is_hide_target(int uid, std::string_view process, int max_len = 1024);

// Hide policies
void hide_daemon(int pid, int client);
void hide_unmount(int pid = -1);
void hide_sensitive_props();
void hide_late_sensitive_props();

enum {
    ENABLE_HIDE,
    DISABLE_HIDE,
    ADD_LIST,
    RM_LIST,
    LS_LIST,
    HIDE_STATUS,
};

enum {
    HIDE_IS_ENABLED = DAEMON_LAST,
    HIDE_NOT_ENABLED,
    HIDE_ITEM_EXIST,
    HIDE_ITEM_NOT_EXIST,
    HIDE_NO_NS,
    HIDE_INVALID_PKG
};
