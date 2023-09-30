typedef long int __off_t;
typedef long int __off64_t;
typedef long int __time_t;
typedef int __clockid_t;
typedef long int __syscall_slong_t;
struct timespec {
    __time_t tv_sec;
    __syscall_slong_t tv_nsec;
} ;
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
extern void free(void *__ptr);
extern void *memcpy(void *__restrict __dest, const void *__restrict __src , size_t __n);
extern int strcmp(const char *__s1, const char *__s2);
struct sigevent ;
extern int clock_gettime(clockid_t __clock_id, struct timespec *__tp);
int input_through_argv(int , char **);
struct timespec start_time;
void begin_timer();
long long time_elapsed();
int input_through_argv(int argc, char *argv[]) {
    if (argc < 3) {
        return 0;
    }
    int _imopVarPre112;
    char *_imopVarPre113;
    int _imopVarPre114;
    _imopVarPre112 = argc == 4;
    if (_imopVarPre112) {
        _imopVarPre113 = argv[1];
        _imopVarPre114 = strcmp(_imopVarPre113, "-");
        _imopVarPre112 = _imopVarPre114 == 0;
    }
    if (_imopVarPre112) {
        return 2;
    }
    return 0;
}
void begin_timer() {
    struct timespec *_imopVarPre116;
    _imopVarPre116 = &start_time;
    clock_gettime(2, _imopVarPre116);
}
long long time_elapsed() {
    struct timespec end_time;
    struct timespec *_imopVarPre118;
    _imopVarPre118 = &end_time;
    clock_gettime(2, _imopVarPre118);
    long long int s = end_time.tv_sec - start_time.tv_sec;
    long long int ns = end_time.tv_nsec - start_time.tv_nsec;
    return (s * ((long long) 1e9)) + ns;
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
    unsigned long int _imopVarPre260;
    void *_imopVarPre261;
    _imopVarPre260 = N * sizeof(process);
    _imopVarPre261 = malloc(_imopVarPre260);
    process *processes = _imopVarPre261;
    unsigned long int _imopVarPre263;
    void *_imopVarPre264;
    _imopVarPre263 = N * sizeof(int);
    _imopVarPre264 = malloc(_imopVarPre263);
    int *ids = _imopVarPre264;
    int i;
    for (i = 0; i < N; i++) {
        ids[i] = i;
    }
    for (i = 0; i < N; i++) {
        int _imopVarPre266;
        _imopVarPre266 = rand();
        int j = _imopVarPre266 % (N - i);
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
int main(int argc, char *argv[]) {
    int N;
    process *processes;
    int iterate;
    int iterations = 1;
    int _imopVarPre342;
    _imopVarPre342 = input_through_argv(argc, argv);
    if ((iterate = _imopVarPre342)) {
        char *_imopVarPre344;
        struct _IO_FILE *_imopVarPre345;
        _imopVarPre344 = argv[2];
        _imopVarPre345 = fopen(_imopVarPre344, "r");
        FILE *in = _imopVarPre345;
        int *_imopVarPre347;
        _imopVarPre347 = &N;
        fscanf(in, "%d", _imopVarPre347);
        processes = generate_nodes(N);
        int i;
        for (i = 0; i < N; i++) {
            int x;
            int *_imopVarPre349;
            _imopVarPre349 = &x;
            fscanf(in, "%d", _imopVarPre349);
            processes[i].id = processes[i].leader = processes[i].send = x;
        }
        int *_imopVarPre352;
        char *_imopVarPre353;
        _imopVarPre352 = &iterations;
        _imopVarPre353 = argv[3];
        sscanf(_imopVarPre353, "%d", _imopVarPre352);
    } else {
        N = 16;
        if (argc > 1) {
            int *_imopVarPre356;
            char *_imopVarPre357;
            _imopVarPre356 = &N;
            _imopVarPre357 = argv[1];
            sscanf(_imopVarPre357, "%d", _imopVarPre356);
        }
        processes = generate_nodes(N);
    }
    long long duration = 0;
    int verification;
    int i;
    for (i = 0; i < iterations; i++) {
        struct stUn_imopVarPre30 *processes_imopVarPre2;
        int i_imopVarPre3;
        int i_imopVarPre1;
        struct stUn_imopVarPre30 *processes_imopVarPre4;
        int i_imopVarPre5;
        struct stUn_imopVarPre30 *processes_imopVarPre0;
        int condVar_imopVarPre9;
        int _imopVarPre361;
        struct stUn_imopVarPre30 *processes_imopVarPre6;
        int i_imopVarPre7;
        int chosen_id_imopVarPre8 = -1;
        struct stUn_imopVarPre30 *_imopVarPre358;
        _imopVarPre358 = generate_nodes(N);
        /**/
        process *ps = _imopVarPre358;
#pragma omp parallel
        {
            unsigned long int _imopVarPre360;
#pragma omp master
            {
                _imopVarPre360 = sizeof(process) * N;
                memcpy(ps, processes, _imopVarPre360);
                begin_timer();
            }
            int r;
#pragma omp master
            {
                r = 0;
                condVar_imopVarPre9 = (r < N);
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            for (; condVar_imopVarPre9; ) {
#pragma omp master
                {
                    processes_imopVarPre4 = ps;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                for (i_imopVarPre5 = 0; i_imopVarPre5 < N; i_imopVarPre5++) {
                    int next = (i_imopVarPre5 + 1) % N;
                    processes_imopVarPre4[next].received = processes_imopVarPre4[i_imopVarPre5].send;
                }
#pragma omp master
                {
                    processes_imopVarPre0 = ps;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                for (i_imopVarPre1 = 0; i_imopVarPre1 < N; i_imopVarPre1++) {
                    if (processes_imopVarPre0[i_imopVarPre1].received > processes_imopVarPre0[i_imopVarPre1].leader) {
                        processes_imopVarPre0[i_imopVarPre1].send = processes_imopVarPre0[i_imopVarPre1].received;
                        processes_imopVarPre0[i_imopVarPre1].leader = processes_imopVarPre0[i_imopVarPre1].received;
                    } else {
                        if (processes_imopVarPre0[i_imopVarPre1].received == processes_imopVarPre0[i_imopVarPre1].id) {
                            processes_imopVarPre0[i_imopVarPre1].leader = processes_imopVarPre0[i_imopVarPre1].id;
                            processes_imopVarPre0[i_imopVarPre1].status = 1;
                        }
                    }
                }
#pragma omp master
                {
                    r++;
                    condVar_imopVarPre9 = (r < N);
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            }
#pragma omp master
            {
                processes_imopVarPre6 = ps;
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
            for (i_imopVarPre7 = 0; i_imopVarPre7 < N; i_imopVarPre7++) {
                if (processes_imopVarPre6[i_imopVarPre7].status == 1) {
                    chosen_id_imopVarPre8 = i_imopVarPre7;
                }
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
            {
                _imopVarPre361 = chosen_id_imopVarPre8;
            }
        }
        int chosen_id = _imopVarPre361;
        if (chosen_id == -1) {
            ;
            verification = 1;
            break;
        }
#pragma omp parallel
        {
#pragma omp master
            {
                processes_imopVarPre2 = ps;
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            signed long long int _imopVarPre362;
#pragma omp for nowait
            for (i_imopVarPre3 = 0; i_imopVarPre3 < N; i_imopVarPre3++) {
                processes_imopVarPre2[i_imopVarPre3].leader = chosen_id;
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
            {
                ;
                _imopVarPre362 = time_elapsed();
                duration += _imopVarPre362;
                free(ps);
                verification = 0;
            }
        }
    }
    if (iterate) {
        double _imopVarPre364;
        _imopVarPre364 = ((double) duration) / iterations;
        printf("%.2lf\n", _imopVarPre364);
    }
    return verification;
}
