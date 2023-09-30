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
extern void omp_set_lock(omp_lock_t *);
extern void omp_unset_lock(omp_lock_t *);
typedef long unsigned int size_t;
struct _IO_FILE ;
struct _IO_FILE ;
struct _IO_jump_t ;
struct _IO_FILE ;
struct _IO_FILE_plus ;
extern int printf(const char *__restrict __format, ...);
extern int sscanf(const char *__restrict __s, const char *__restrict __format, ...);
extern int sscanf(const char *__restrict __s, const char *__restrict __format, ...);
typedef __clockid_t clockid_t;
extern int rand(void );
extern void srand(unsigned int __seed);
extern void *malloc(size_t __size);
extern void *realloc(void *__ptr, size_t __size);
extern void *memcpy(void *__restrict __dest, const void *__restrict __src , size_t __n);
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
struct timespec start_time;
void begin_timer();
long long time_elapsed();
void begin_timer() {
    struct timespec *_imopVarPre118;
    _imopVarPre118 = &start_time;
    clock_gettime(2, _imopVarPre118);
}
long long time_elapsed() {
    struct timespec end_time;
    struct timespec *_imopVarPre120;
    _imopVarPre120 = &end_time;
    clock_gettime(2, _imopVarPre120);
    long long int s = end_time.tv_sec - start_time.tv_sec;
    long long int ns = end_time.tv_nsec - start_time.tv_nsec;
    return (s * ((long long) 1e9)) + ns;
}
void initialize_vector(vector *v, size_t object_size) {
    v->object_size = object_size;
    v->allocated = 16;
    unsigned long int _imopVarPre122;
    void *_imopVarPre123;
    _imopVarPre122 = v->allocated * v->object_size;
    _imopVarPre123 = malloc(_imopVarPre122);
    v->items = _imopVarPre123;
    v->used = 0;
}
vector *new_vector(size_t object_size) {
    unsigned long int _imopVarPre126;
    void *_imopVarPre127;
    _imopVarPre126 = 1 * sizeof(vector);
    _imopVarPre127 = malloc(_imopVarPre126);
    vector *v = (vector *) _imopVarPre127;
    initialize_vector(v, object_size);
    return v;
}
void append_to_vector(vector *v, void *object) {
    while (v->allocated <= v->used + 1) {
        v->allocated *= 2;
        unsigned long int _imopVarPre130;
        void *_imopVarPre131;
        void *_imopVarPre132;
        _imopVarPre130 = v->allocated * v->object_size;
        _imopVarPre131 = v->items;
        _imopVarPre132 = realloc(_imopVarPre131, _imopVarPre130);
        v->items = _imopVarPre132;
    }
    unsigned long int _imopVarPre135;
    void *_imopVarPre136;
    _imopVarPre135 = v->object_size;
    _imopVarPre136 = v->items + (v->used * v->object_size);
    memcpy(_imopVarPre136, object, _imopVarPre135);
    v->used++;
}
void *elem_at(vector *v, size_t idx) {
    return v->items + (idx * v->object_size);
}
omp_lock_t *new_locks(int );
omp_lock_t *new_locks(int N) {
    ;
    unsigned long int _imopVarPre303;
    void *_imopVarPre304;
    _imopVarPre303 = N * sizeof(omp_lock_t);
    _imopVarPre304 = malloc(_imopVarPre303);
    omp_lock_t *locks = _imopVarPre304;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre4 *_imopVarPre306;
        _imopVarPre306 = locks + i;
        omp_init_lock(_imopVarPre306);
// #pragma omp dummyFlush LOCK_WRITE_END
    }
    return locks;
}
struct stUn_imopVarPre31 {
    int N;
    vector **queues;
    int *front;
    omp_lock_t *locks;
} ;
typedef struct stUn_imopVarPre31 queuelist;
queuelist *new_queuelist(int , size_t );
void enqueue(queuelist *, int  , void *);
void *dequeue(queuelist *, int );
int is_ql_queue_empty(queuelist *, int );
queuelist *new_queuelist(int N, size_t elem_size) {
    unsigned long int _imopVarPre310;
    void *_imopVarPre311;
    _imopVarPre310 = sizeof(queuelist);
    _imopVarPre311 = malloc(_imopVarPre310);
    queuelist *ql = _imopVarPre311;
    ql->N = N;
    ;
    unsigned long int _imopVarPre313;
    void *_imopVarPre314;
    _imopVarPre313 = N * sizeof(vector *);
    _imopVarPre314 = malloc(_imopVarPre313);
    vector **queues = _imopVarPre314;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre315;
        _imopVarPre315 = new_vector(elem_size);
        queues[i] = _imopVarPre315;
    }
    ql->queues = queues;
    unsigned long int _imopVarPre317;
    void *_imopVarPre318;
    _imopVarPre317 = N * sizeof(int);
    _imopVarPre318 = malloc(_imopVarPre317);
    ql->front = _imopVarPre318;
    for (i = 0; i < N; i++) {
        ql->front[i] = 0;
    }
    struct stUn_imopVarPre4 *_imopVarPre319;
    _imopVarPre319 = new_locks(N);
    ql->locks = _imopVarPre319;
    return ql;
}
void enqueue(queuelist *ql, int i , void *object) {
    struct stUn_imopVarPre4 *_imopVarPre327;
    _imopVarPre327 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre327);
// #pragma omp dummyFlush LOCK_MODIFY_END
    struct stUn_imopVarPre27 *_imopVarPre329;
    _imopVarPre329 = ql->queues[i];
    append_to_vector(_imopVarPre329, object);
    struct stUn_imopVarPre4 *_imopVarPre331;
    _imopVarPre331 = ql->locks + i;
    omp_unset_lock(_imopVarPre331);
// #pragma omp dummyFlush LOCK_WRITE_END
}
void *dequeue(queuelist *ql, int i) {
    struct stUn_imopVarPre4 *_imopVarPre333;
    _imopVarPre333 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre333);
// #pragma omp dummyFlush LOCK_MODIFY_END
    void *object;
    vector *v = ql->queues[i];
    if (ql->front[i] == v->used) {
        object = ((void *) 0);
        goto end;
    }
    int _imopVarPre335;
    void *_imopVarPre336;
    _imopVarPre335 = ql->front[i];
    _imopVarPre336 = elem_at(v, _imopVarPre335);
    object = _imopVarPre336;
    ql->front[i]++;
    if (ql->front[i] == v->used) {
        ql->front[i] = 0;
        v->used = 0;
    }
    struct stUn_imopVarPre4 *_imopVarPre338;
    end: _imopVarPre338 = ql->locks + i;
    omp_unset_lock(_imopVarPre338);
// #pragma omp dummyFlush LOCK_WRITE_END
    return object;
}
int is_ql_queue_empty(queuelist *ql, int i) {
    struct stUn_imopVarPre4 *_imopVarPre340;
    _imopVarPre340 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre340);
// #pragma omp dummyFlush LOCK_MODIFY_END
    vector *v = ql->queues[i];
    int result = ql->front[i] == v->used;
    struct stUn_imopVarPre4 *_imopVarPre342;
    _imopVarPre342 = ql->locks + i;
    omp_unset_lock(_imopVarPre342);
// #pragma omp dummyFlush LOCK_WRITE_END
    return result;
}
struct stUn_imopVarPre32 {
    int b;
    int vote;
    int good;
    int d;
    int decided;
} ;
typedef struct stUn_imopVarPre32 processor;
struct stUn_imopVarPre33 {
    int from;
    int vote;
} ;
typedef struct stUn_imopVarPre33 vote;
processor *new_processors(int N) {
    ;
    unsigned long int _imopVarPre344;
    void *_imopVarPre345;
    _imopVarPre344 = N * sizeof(processor);
    _imopVarPre345 = malloc(_imopVarPre344);
    processor *processors = _imopVarPre345;
    ;
    int i;
    for (i = 0; i < N; i++) {
        processor *p = processors + i;
        int _imopVarPre347;
        _imopVarPre347 = rand();
        p->good = _imopVarPre347 % 100 > 10;
        int _imopVarPre349;
        _imopVarPre349 = rand();
        p->b = _imopVarPre349 % 2;
        p->vote = p->b;
        p->d = 0;
        p->decided = 0;
    }
    return processors;
}
int verify_and_print_solution(processor *processors, int N) {
    int yes = 0;
    int no = 0;
    int good = 0;
    int i;
    for (i = 0; i < N; i++) {
        processor *p = processors + i;
        if (p->good) {
            good++;
        }
        int _imopVarPre358;
        _imopVarPre358 = p->decided;
        if (_imopVarPre358) {
            _imopVarPre358 = p->good;
        }
        if (_imopVarPre358) {
            if (p->d == 0) {
                no++;
            } else {
                yes++;
            }
        }
    }
    if (yes + no != good) {
        ;
        return 1;
    }
    int _imopVarPre360;
    _imopVarPre360 = yes != 0;
    if (_imopVarPre360) {
        _imopVarPre360 = no != 0;
    }
    if (_imopVarPre360) {
        ;
        return 1;
    }
    ;
    return 0;
}
int main(int argc, char *argv[]) {
    int N = 16;
    if (argc > 1) {
        int *_imopVarPre363;
        char *_imopVarPre364;
        _imopVarPre363 = &N;
        _imopVarPre364 = argv[1];
        sscanf(_imopVarPre364, "%d", _imopVarPre363);
    }
    srand(0);
    long long duration = 0;
    begin_timer();
    struct stUn_imopVarPre32 *_imopVarPre365;
    _imopVarPre365 = new_processors(N);
    processor *processors = _imopVarPre365;
    unsigned long int _imopVarPre367;
    struct stUn_imopVarPre31 *_imopVarPre368;
    _imopVarPre367 = sizeof(vote);
    _imopVarPre368 = new_queuelist(N, _imopVarPre367);
    queuelist *vote_ql = _imopVarPre368;
    int ret;
    int _imopVarPre370;
    int i_imopVarPre0;
    int i;
    int i_imopVarPre1;
    while (1) {
#pragma omp parallel
        {
#pragma omp master
            {
                ret = 0;
                ;
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
            for (i_imopVarPre0 = 0; i_imopVarPre0 < N; i_imopVarPre0++) {
                processor *p = processors + i_imopVarPre0;
                if (p->decided == 0) {
                    ret = 1;
                }
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
            {
                ;
                _imopVarPre370 = ret;
            }
        }
        if (!_imopVarPre370) {
            break;
        }
#pragma omp parallel
        {
#pragma omp master
            {
                ;
            }
#pragma omp for nowait
            for (i = 0; i < N; i++) {
                processor *p = processors + i;
                int j;
                for (j = 0; j < N; j++) {
                    if (i == j) {
                        continue;
                    }
                    vote v = {i, p->vote};
                    struct stUn_imopVarPre33 *_imopVarPre351;
                    _imopVarPre351 = &v;
                    enqueue(vote_ql, j, _imopVarPre351);
                }
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
            for (i_imopVarPre1 = 0; i_imopVarPre1 < N; i_imopVarPre1++) {
                processor *p = processors + i_imopVarPre1;
                int yes = 0;
                int no = 0;
                int _imopVarPre353;
                _imopVarPre353 = is_ql_queue_empty(vote_ql, i_imopVarPre1);
                while (!_imopVarPre353) {
                    void *_imopVarPre354;
                    _imopVarPre354 = dequeue(vote_ql, i_imopVarPre1);
                    vote *v = _imopVarPre354;
                    if (v->vote) {
                        yes++;
                    } else {
                        no++;
                    }
                    _imopVarPre353 = is_ql_queue_empty(vote_ql, i_imopVarPre1);
                }
                int maj = 1;
                int tally = yes;
                if (no > yes) {
                    maj = 0;
                    tally = no;
                }
                int threshold;
                int _imopVarPre356;
                _imopVarPre356 = rand();
                if (_imopVarPre356 % 2 == 0) {
                    threshold = ((5 * N) / 8 + 1);
                } else {
                    threshold = ((3 * N) / 4 + 1);
                }
                if (tally > threshold) {
                    p->vote = maj;
                } else {
                    p->vote = 0;
                }
                if (tally >= ((7 * N) / 8)) {
                    p->decided = 1;
                    p->d = maj;
                }
            }
        }
    }
    signed long long int _imopVarPre371;
    _imopVarPre371 = time_elapsed();
    duration += _imopVarPre371;
    double _imopVarPre373;
    _imopVarPre373 = ((double) duration);
    printf("%.2lf\n", _imopVarPre373);
    int _imopVarPre374;
    _imopVarPre374 = verify_and_print_solution(processors, N);
    return _imopVarPre374;
}
