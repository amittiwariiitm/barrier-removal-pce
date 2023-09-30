typedef long int __off_t;
typedef long int __off64_t;
typedef long int __time_t;
typedef int __clockid_t;
typedef long int __syscall_slong_t;
struct timespec {
    __time_t tv_sec;
    __syscall_slong_t tv_nsec;
} ;
struct stUn_imopVarPre4 {
    unsigned char _x[4];
} ;
typedef struct stUn_imopVarPre4 omp_lock_t;
extern void omp_init_lock(omp_lock_t *);
extern void omp_destroy_lock(omp_lock_t *);
extern void omp_set_lock(omp_lock_t *);
extern void omp_unset_lock(omp_lock_t *);
typedef long unsigned int size_t;
struct _IO_FILE ;
struct _IO_FILE ;
typedef struct _IO_FILE FILE;
struct _IO_jump_t ;
struct _IO_FILE ;
typedef void _IO_lock_t;
struct _IO_FILE {
    int _flags;
    char *_IO_read_ptr;
    char *_IO_read_end;
    char *_IO_read_base;
    char *_IO_write_base;
    char *_IO_write_ptr;
    char *_IO_write_end;
    char *_IO_buf_base;
    char *_IO_buf_end;
    char *_IO_save_base;
    char *_IO_backup_base;
    char *_IO_save_end;
    struct _IO_marker *_markers;
    struct _IO_FILE *_chain;
    int _fileno;
    int _flags2;
    __off_t _old_offset;
    unsigned short _cur_column;
    signed char _vtable_offset;
    char _shortbuf[1];
    _IO_lock_t *_lock;
    __off64_t _offset;
    void *__pad1;
    void *__pad2;
    void *__pad3;
    void *__pad4;
    size_t __pad5;
    int _mode;
    char _unused2[15 * sizeof(int) - 4 * sizeof(void *) - sizeof(size_t)];
} ;
struct _IO_FILE_plus ;
extern FILE *fopen(const char *__restrict __filename, const char *__restrict __modes);
extern int printf(const char *__restrict __format, ...);
extern int fscanf(FILE *__restrict __stream, const char *__restrict __format, ...);
extern int sscanf(const char *__restrict __s, const char *__restrict __format, ...);
extern int fscanf(FILE *__restrict __stream, const char *__restrict __format, ...);
extern int sscanf(const char *__restrict __s, const char *__restrict __format, ...);
typedef __clockid_t clockid_t;
extern int rand(void );
extern void *malloc(size_t __size);
extern void *realloc(void *__ptr, size_t __size);
extern void free(void *__ptr);
extern void *memcpy(void *__restrict __dest, const void *__restrict __src , size_t __n);
extern int strcmp(const char *__s1, const char *__s2);
struct sigevent ;
extern int clock_gettime(clockid_t __clock_id, struct timespec *__tp);
struct stUn_imopVarPre27 {
    void *items;
    size_t object_size;
    int allocated;
    int used;
} ;
typedef struct stUn_imopVarPre27 vector;
void initialize_vector(vector *, size_t );
vector *new_vector(size_t );
void append_to_vector(vector *, void *);
void *elem_at(vector *, size_t );
void free_vector(vector *);
int input_through_argv(int , char **);
struct timespec start_time;
void begin_timer();
long long time_elapsed();
int input_through_argv(int argc, char *argv[]) {
    if (argc < 3) {
        return 0;
    }
    int _imopVarPre113;
    char *_imopVarPre114;
    int _imopVarPre115;
    _imopVarPre113 = argc == 4;
    if (_imopVarPre113) {
        _imopVarPre114 = argv[1];
        _imopVarPre115 = strcmp(_imopVarPre114, "-");
        _imopVarPre113 = _imopVarPre115 == 0;
    }
    if (_imopVarPre113) {
        return 2;
    }
    return 0;
}
void begin_timer() {
    struct timespec *_imopVarPre117;
    _imopVarPre117 = &start_time;
    clock_gettime(2, _imopVarPre117);
}
long long time_elapsed() {
    struct timespec end_time;
    struct timespec *_imopVarPre119;
    _imopVarPre119 = &end_time;
    clock_gettime(2, _imopVarPre119);
    long long int s = end_time.tv_sec - start_time.tv_sec;
    long long int ns = end_time.tv_nsec - start_time.tv_nsec;
    return (s * ((long long) 1e9)) + ns;
}
void initialize_vector(vector *v, size_t object_size) {
    v->object_size = object_size;
    v->allocated = 16;
    unsigned long int _imopVarPre121;
    void *_imopVarPre122;
    _imopVarPre121 = v->allocated * v->object_size;
    _imopVarPre122 = malloc(_imopVarPre121);
    v->items = _imopVarPre122;
    v->used = 0;
}
vector *new_vector(size_t object_size) {
    unsigned long int _imopVarPre125;
    void *_imopVarPre126;
    _imopVarPre125 = 1 * sizeof(vector);
    _imopVarPre126 = malloc(_imopVarPre125);
    vector *v = (vector *) _imopVarPre126;
    initialize_vector(v, object_size);
    return v;
}
void append_to_vector(vector *v, void *object) {
    while (v->allocated <= v->used + 1) {
        v->allocated *= 2;
        unsigned long int _imopVarPre129;
        void *_imopVarPre130;
        void *_imopVarPre131;
        _imopVarPre129 = v->allocated * v->object_size;
        _imopVarPre130 = v->items;
        _imopVarPre131 = realloc(_imopVarPre130, _imopVarPre129);
        v->items = _imopVarPre131;
    }
    unsigned long int _imopVarPre134;
    void *_imopVarPre135;
    _imopVarPre134 = v->object_size;
    _imopVarPre135 = v->items + (v->used * v->object_size);
    memcpy(_imopVarPre135, object, _imopVarPre134);
    v->used++;
}
void *elem_at(vector *v, size_t idx) {
    return v->items + (idx * v->object_size);
}
void free_vector(vector *v) {
    void *_imopVarPre137;
    _imopVarPre137 = v->items;
    free(_imopVarPre137);
    free(v);
}
struct stUn_imopVarPre30 {
    int id;
    int send;
    int received;
    int status;
    int leader;
} ;
typedef struct stUn_imopVarPre30 process;
process *generate_nodes(int );
process *generate_nodes(int N) {
    unsigned long int _imopVarPre261;
    void *_imopVarPre262;
    _imopVarPre261 = N * sizeof(process);
    _imopVarPre262 = malloc(_imopVarPre261);
    process *processes = _imopVarPre262;
    unsigned long int _imopVarPre264;
    void *_imopVarPre265;
    _imopVarPre264 = N * sizeof(int);
    _imopVarPre265 = malloc(_imopVarPre264);
    int *ids = _imopVarPre265;
    int i;
    for (i = 0; i < N; i++) {
        ids[i] = i;
    }
    for (i = 0; i < N; i++) {
        int _imopVarPre267;
        _imopVarPre267 = rand();
        int j = _imopVarPre267 % (N - i);
        int t = ids[i];
        ids[i] = ids[j];
        ids[j] = t;
    }
    for (i = 0; i < N; i++) {
        processes[i].id = ids[i];
        processes[i].received = -1;
        processes[i].send = ids[i];
        processes[i].status = 0;
        processes[i].leader = ids[i];
    }
    free(ids);
    return processes;
}
omp_lock_t *new_locks(int );
void free_locks(omp_lock_t *, int );
omp_lock_t *new_locks(int N) {
    ;
    unsigned long int _imopVarPre302;
    void *_imopVarPre303;
    _imopVarPre302 = N * sizeof(omp_lock_t);
    _imopVarPre303 = malloc(_imopVarPre302);
    omp_lock_t *locks = _imopVarPre303;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre4 *_imopVarPre305;
        _imopVarPre305 = locks + i;
        omp_init_lock(_imopVarPre305);
// #pragma omp dummyFlush LOCK_WRITE_END
    }
    return locks;
}
void free_locks(omp_lock_t *locks, int N) {
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre4 *_imopVarPre307;
        _imopVarPre307 = locks + i;
        omp_destroy_lock(_imopVarPre307);
    }
    ;
    free(locks);
}
struct stUn_imopVarPre31 {
    int N;
    vector **queues;
    int *front;
    omp_lock_t *locks;
} ;
typedef struct stUn_imopVarPre31 queuelist;
queuelist *new_queuelist(int , size_t );
void free_queuelist(queuelist *);
void enqueue(queuelist *, int  , void *);
void *dequeue(queuelist *, int );
int is_ql_queue_empty(queuelist *, int );
queuelist *new_queuelist(int N, size_t elem_size) {
    unsigned long int _imopVarPre309;
    void *_imopVarPre310;
    _imopVarPre309 = sizeof(queuelist);
    _imopVarPre310 = malloc(_imopVarPre309);
    queuelist *ql = _imopVarPre310;
    ql->N = N;
    ;
    unsigned long int _imopVarPre312;
    void *_imopVarPre313;
    _imopVarPre312 = N * sizeof(vector *);
    _imopVarPre313 = malloc(_imopVarPre312);
    vector **queues = _imopVarPre313;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre314;
        _imopVarPre314 = new_vector(elem_size);
        queues[i] = _imopVarPre314;
    }
    ql->queues = queues;
    unsigned long int _imopVarPre316;
    void *_imopVarPre317;
    _imopVarPre316 = N * sizeof(int);
    _imopVarPre317 = malloc(_imopVarPre316);
    ql->front = _imopVarPre317;
    for (i = 0; i < N; i++) {
        ql->front[i] = 0;
    }
    struct stUn_imopVarPre4 *_imopVarPre318;
    _imopVarPre318 = new_locks(N);
    ql->locks = _imopVarPre318;
    return ql;
}
void free_queuelist(queuelist *ql) {
    int N = ql->N;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre320;
        _imopVarPre320 = ql->queues[i];
        free_vector(_imopVarPre320);
    }
    ;
    int *_imopVarPre322;
    _imopVarPre322 = ql->front;
    free(_imopVarPre322);
    ;
    struct stUn_imopVarPre4 *_imopVarPre324;
    _imopVarPre324 = ql->locks;
    free_locks(_imopVarPre324, N);
    ;
    free(ql);
}
void enqueue(queuelist *ql, int i , void *object) {
    struct stUn_imopVarPre4 *_imopVarPre326;
    _imopVarPre326 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre326);
// #pragma omp dummyFlush LOCK_MODIFY_END
    struct stUn_imopVarPre27 *_imopVarPre328;
    _imopVarPre328 = ql->queues[i];
    append_to_vector(_imopVarPre328, object);
    struct stUn_imopVarPre4 *_imopVarPre330;
    _imopVarPre330 = ql->locks + i;
    omp_unset_lock(_imopVarPre330);
// #pragma omp dummyFlush LOCK_WRITE_END
}
void *dequeue(queuelist *ql, int i) {
    struct stUn_imopVarPre4 *_imopVarPre332;
    _imopVarPre332 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre332);
// #pragma omp dummyFlush LOCK_MODIFY_END
    void *object;
    vector *v = ql->queues[i];
    if (ql->front[i] == v->used) {
        object = ((void *) 0);
        goto end;
    }
    int _imopVarPre334;
    void *_imopVarPre335;
    _imopVarPre334 = ql->front[i];
    _imopVarPre335 = elem_at(v, _imopVarPre334);
    object = _imopVarPre335;
    ql->front[i]++;
    if (ql->front[i] == v->used) {
        ql->front[i] = 0;
        v->used = 0;
    }
    struct stUn_imopVarPre4 *_imopVarPre337;
    end: _imopVarPre337 = ql->locks + i;
    omp_unset_lock(_imopVarPre337);
// #pragma omp dummyFlush LOCK_WRITE_END
    return object;
}
int is_ql_queue_empty(queuelist *ql, int i) {
    struct stUn_imopVarPre4 *_imopVarPre339;
    _imopVarPre339 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre339);
// #pragma omp dummyFlush LOCK_MODIFY_END
    vector *v = ql->queues[i];
    int result = ql->front[i] == v->used;
    struct stUn_imopVarPre4 *_imopVarPre341;
    _imopVarPre341 = ql->locks + i;
    omp_unset_lock(_imopVarPre341);
// #pragma omp dummyFlush LOCK_WRITE_END
    return result;
}
struct stUn_imopVarPre32 {
    int starter_label;
    int hops_left;
    int direction;
    int direction_changed;
    int stop_initiating;
} ;
typedef struct stUn_imopVarPre32 message;
int check_statuses(process *processes, int N , queuelist *send_ql) {
    int i;
    for (i = 0; i < N; i++) {
        process *p = processes + i;
        if (p->status == 3) {
            return -i;
        }
    }
    for (i = 0; i < N; i++) {
        int _imopVarPre356;
        _imopVarPre356 = is_ql_queue_empty(send_ql, i);
        if (!_imopVarPre356) {
            return 1;
        }
    }
    return 2;
}
int main(int argc, char *argv[]) {
    int N;
    process *processes;
    int iterate;
    int iterations = 1;
    int _imopVarPre359;
    _imopVarPre359 = input_through_argv(argc, argv);
    if ((iterate = _imopVarPre359)) {
        char *_imopVarPre361;
        struct _IO_FILE *_imopVarPre362;
        _imopVarPre361 = argv[2];
        _imopVarPre362 = fopen(_imopVarPre361, "r");
        FILE *in = _imopVarPre362;
        int *_imopVarPre364;
        _imopVarPre364 = &N;
        fscanf(in, "%d", _imopVarPre364);
        processes = generate_nodes(N);
        int i;
        for (i = 0; i < N; i++) {
            int x;
            int *_imopVarPre366;
            _imopVarPre366 = &x;
            fscanf(in, "%d", _imopVarPre366);
            processes[i].id = processes[i].leader = processes[i].send = x;
        }
        int *_imopVarPre369;
        char *_imopVarPre370;
        _imopVarPre369 = &iterations;
        _imopVarPre370 = argv[3];
        sscanf(_imopVarPre370, "%d", _imopVarPre369);
    } else {
        N = 16;
        if (argc > 1) {
            int *_imopVarPre373;
            char *_imopVarPre374;
            _imopVarPre373 = &N;
            _imopVarPre374 = argv[1];
            sscanf(_imopVarPre374, "%d", _imopVarPre373);
        }
        processes = generate_nodes(N);
    }
    long long duration = 0;
    int i;
    for (i = 0; i < iterations; i++) {
        struct stUn_imopVarPre30 *_imopVarPre375;
        _imopVarPre375 = generate_nodes(N);
        process *ps = _imopVarPre375;
        unsigned long int _imopVarPre377;
        _imopVarPre377 = sizeof(process) * N;
        memcpy(ps, processes, _imopVarPre377);
        unsigned long int _imopVarPre379;
        struct stUn_imopVarPre31 *_imopVarPre380;
        _imopVarPre379 = sizeof(message);
        _imopVarPre380 = new_queuelist(N, _imopVarPre379);
        queuelist *recv_ql = _imopVarPre380;
        unsigned long int _imopVarPre382;
        struct stUn_imopVarPre31 *_imopVarPre383;
        _imopVarPre382 = sizeof(message);
        _imopVarPre383 = new_queuelist(N, _imopVarPre382);
        queuelist *send_ql = _imopVarPre383;
        begin_timer();
        int chosen_id = -1;
        int l = 0;
        int finished = 0;
        int i_imopVarPre5;
        struct stUn_imopVarPre30 *processes_imopVarPre4;
        int i_imopVarPre1;
        int _imopVarPre384;
        struct stUn_imopVarPre30 *processes_imopVarPre0;
        while (!finished) {
#pragma omp parallel
            {
#pragma omp master
                {
                    l += 1;
                    ;
                    processes_imopVarPre4 = ps;
                    ;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                for (i_imopVarPre5 = 0; i_imopVarPre5 < N; i_imopVarPre5++) {
                    process *p = processes_imopVarPre4 + i_imopVarPre5;
                    if (p->status == -1) {
                        continue;
                    }
                    message to_right = {i_imopVarPre5, 1 << l , 1 , 0 , 0};
                    message to_left = {i_imopVarPre5, 1 << l , -1 , 0 , 0};
                    struct stUn_imopVarPre32 *_imopVarPre343;
                    _imopVarPre343 = &to_right;
                    enqueue(send_ql, i_imopVarPre5, _imopVarPre343);
                    struct stUn_imopVarPre32 *_imopVarPre345;
                    _imopVarPre345 = &to_left;
                    enqueue(send_ql, i_imopVarPre5, _imopVarPre345);
                }
            }
            while (1) {
#pragma omp parallel
                {
#pragma omp master
                    {
                        processes_imopVarPre0 = ps;
                        ;
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                    for (i_imopVarPre1 = 0; i_imopVarPre1 < N; i_imopVarPre1++) {
                        ;
                        process *p = processes_imopVarPre0 + i_imopVarPre1;
                        int _imopVarPre347;
                        _imopVarPre347 = is_ql_queue_empty(send_ql, i_imopVarPre1);
                        while (!_imopVarPre347) {
                            void *_imopVarPre348;
                            _imopVarPre348 = dequeue(send_ql, i_imopVarPre1);
                            message *m = _imopVarPre348;
                            ;
                            int _imopVarPre350;
                            _imopVarPre350 = m->starter_label == i_imopVarPre1;
                            if (_imopVarPre350) {
                                _imopVarPre350 = m->hops_left != (1 << l);
                            }
                            if (_imopVarPre350) {
                                if (m->stop_initiating) {
                                    p->status = -1;
                                } else {
                                    if (m->direction_changed) {
                                        p->status++;
                                    } else {
                                        p->status = 3;
                                        break;
                                    }
                                }
                                continue;
                            }
                            if (m->hops_left == 0) {
                                ;
                                m->hops_left = 1 << l;
                                m->direction *= -1;
                                m->direction_changed = 1;
                            }
                            if (m->starter_label < i_imopVarPre1) {
                                m->hops_left = (1 << l) - m->hops_left;
                                m->direction *= -1;
                                m->direction_changed = 1;
                                m->stop_initiating = 1;
                                continue;
                            } else {
                                m->hops_left--;
                                p->status = -1;
                            }
                            int next_label = (N + i_imopVarPre1 + m->direction) % N;
                            enqueue(recv_ql, next_label, m);
                            _imopVarPre347 = is_ql_queue_empty(send_ql, i_imopVarPre1);
                        }
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                    for (i_imopVarPre1 = 0; i_imopVarPre1 < N; i_imopVarPre1++) {
                        processes_imopVarPre0 + i_imopVarPre1;
                        int _imopVarPre352;
                        _imopVarPre352 = is_ql_queue_empty(recv_ql, i_imopVarPre1);
                        while (!_imopVarPre352) {
                            void *_imopVarPre354;
                            _imopVarPre354 = dequeue(recv_ql, i_imopVarPre1);
                            enqueue(send_ql, i_imopVarPre1, _imopVarPre354);
                            _imopVarPre352 = is_ql_queue_empty(recv_ql, i_imopVarPre1);
                        }
                    }
                }
                _imopVarPre384 = check_statuses(ps, N, send_ql);
                int status = _imopVarPre384;
                ;
                if (status == 1) {
                    continue;
                }
                if (status == 2) {
                    break;
                }
                if (status <= 0) {
                    int i_imopVarPre3;
                    struct stUn_imopVarPre30 *processes_imopVarPre2;
#pragma omp parallel
                    {
#pragma omp master
                        {
                            chosen_id = -status;
                            processes_imopVarPre2 = ps;
                        }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                        for (i_imopVarPre3 = 0; i_imopVarPre3 < N; i_imopVarPre3++) {
                            processes_imopVarPre2[i_imopVarPre3].leader = chosen_id;
                        }
#pragma omp master
                        {
                            finished = 1;
                        }
                    }
                    break;
                }
            }
        }
        signed long long int _imopVarPre385;
        _imopVarPre385 = time_elapsed();
        duration += _imopVarPre385;
        ;
        ;
        free_queuelist(send_ql);
        free_queuelist(recv_ql);
        free(ps);
    }
    if (iterate) {
        double _imopVarPre387;
        _imopVarPre387 = ((double) duration) / iterations;
        printf("%.2lf\n", _imopVarPre387);
    }
    return 0;
}
