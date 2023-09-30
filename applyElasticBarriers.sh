#!/bin/bash
# Obtain the root folder containing the test-programs to be run.
BASE=$(pwd)
TESTROOT=$1
if [ "${TESTROOT}" == "" ]
then
	echo -e "usage: bash post-pass-only.sh <path-to-test-programs-folder> \nExiting..."
	exit
fi
TESTROOT="$BASE/$1"
if [ ! -d $TESTROOT ]
then
	echo "Could not find the test folder $TESTROOT"
	exit
fi

# Ensure that Z3 has been set correctly.
if [ "${Z3HOME}" == "" ]
then
	echo -e "Set the environment variable Z3HOME to point to the directory z3.\nExiting..."
	exit
fi
if [ ! -d $Z3HOME ]
then
	echo "$Z3HOME does not exist."
	exit
fi

# Obtain IMOPHOME.
SCRPATH="${BASE}/$(dirname $0)"
cd ${SCRPATH}/..
IMOPHOME=$(pwd)

# Create a temporary file for stderr.
DATE=$(date | tr ' ' '_' | tr ':' '_')
ERRFILE="/tmp/errStream${DATE}.err"
echo "Saving standard error stream at ${ERRFILE}."

function generate-independent-costs() {
	echo "Bye"
}

cd $TESTROOT
for FILE in $(ls *.c)
do
	echo -e "Preprocessing $FILE using GCC"
	echo -e "Preprocessing $FILE using GCC" >> $ERRFILE
	FILENAME=$(echo $FILE | cut -f1 -d.)
	PREFILE=${TESTROOT}/${FILENAME}.i
	if [ -f $PREFILE ]
	then
		continue
	fi
	gcc -P -E $FILE -o $PREFILE
done

cd $TESTROOT
for FILE in $(ls *.i)
do
	echo -e "Processing $FILE"
	echo "Processing $FILE" >> $ERRFILE
	cd $TESTROOT
	FILENAME=$(echo $FILE | cut -f1 -d.)
	cd ${IMOPHOME}/bin
	java -ea -Xms2048M -Xmx4096M -cp ${IMOPHOME}/third-party-tools/com.microsoft.z3.jar:. \
		-Djava.library.path=${Z3HOME}/build imop.Main --prepass -ru -dln -f $TESTROOT/$FILE 2>> $ERRFILE

	if [ ! -f "../output-dump/${FILENAME}-postpass.i" ]
	then
		echo "Could not generate the postpass file. Exiting.."
		echo -e "DUMP: $(cat $ERRFILE)"
		continue
	fi

	#PREFILE=/tmp/${FILENAME}.i
	PREFILE=/tmp/cs.i
	echo -e "\n\t\t *** After pre-pass ***"
	echo -e "\t\t *** PHASE-I: Generating Elastic Structure ***"
	rm -f ../output-dump/cs-elastruct.i 2> /dev/null
	cp ../output-dump/${FILENAME}-postpass.i $PREFILE
	java -ea -Xms2048M -Xmx4096M -cp ${IMOPHOME}/third-party-tools/com.microsoft.z3.jar:.\
		-Djava.library.path=${Z3HOME}/build elasticBarrier.WorkOnImop --noPrepass -dln --elastruct -f $PREFILE 2>> $ERRFILE
	cp ../output-dump/cs-elastruct.i ../output-dump/elastruct/${FILENAME}-elastruct.i

	echo -e "\t\t *** PHASE-II: Identifying Independent and Dependent Parts ***"
	rm -f ../output-dump/cs-label.i
	cp ../output-dump/cs-elastruct.i $PREFILE
	java -ea -Xms2048M -Xmx4096M -cp ${IMOPHOME}/third-party-tools/com.microsoft.z3.jar:.\
		-Djava.library.path=${Z3HOME}/build elasticBarrier.WorkOnImop --noPrepass -dln --label -f $PREFILE 2>> $ERRFILE
	cp ../output-dump/cs-label.i ../output-dump/label/${FILENAME}-label.i

	echo -e "\t\t *** PHASE-III: Putting Timers & Prints for Independent Parts ***"
	rm -f ../output-dump/cs-printf.i
	cp ../output-dump/cs-label.i $PREFILE
	java -ea -Xms2048M -Xmx4096M -cp ${IMOPHOME}/third-party-tools/com.microsoft.z3.jar:.\
		-Djava.library.path=${Z3HOME}/build elasticBarrier.WorkOnImop --noPrepass -dln --printf -f $PREFILE 2>> $ERRFILE
	cp ../output-dump/cs-printf.i ../output-dump/printf/${FILENAME}-printf.i

	echo -e "\t\t *** PHASE-IV.A: Converting #pof to Start-End Loops ***"
	rm -f ../output-dump/cs-startend.i
	cp ../output-dump/cs-label.i $PREFILE
	java -ea -Xms2048M -Xmx4096M -cp ${IMOPHOME}/third-party-tools/com.microsoft.z3.jar:.\
		-Djava.library.path=${Z3HOME}/build elasticBarrier.WorkOnImop --noPrepass -dln -ceg -f $PREFILE 2>> $ERRFILE
	cp ../output-dump/cs-startend.i ../output-dump/startend/${FILENAME}-startend.i

	echo -e "\t\t *** PHASE-IV.B: Converting #pof to Start-End Loops in Struct-Sensitive Manner***"
	rm -f ../output-dump/cs-startendsensitive.i
	cp ../output-dump/cs-label.i $PREFILE
	java -ea -Xms2048M -Xmx4096M -cp ${IMOPHOME}/third-party-tools/com.microsoft.z3.jar:.\
		-Djava.library.path=${Z3HOME}/build elasticBarrier.WorkOnImop --noPrepass -dln -cegs -f $PREFILE 2>> $ERRFILE
	cp ../output-dump/cs-startendsensitive.i ../output-dump/startendsensitive/${FILENAME}-startendsensitive.i


	echo -e "\t\t *** PHASE-PATTERN: Printing the Structure of Start-End-Sensitive Code***"
	rm -f ../output-dump/cs-structure.txt
	cp ../output-dump/cs-startendsensitive.i $PREFILE
	java -ea -Xms2048M -Xmx4096M -cp ${IMOPHOME}/third-party-tools/com.microsoft.z3.jar:.\
		-Djava.library.path=${Z3HOME}/build elasticBarrier.WorkOnImop --noPrepass -dln -strPr -f $PREFILE 2>> $ERRFILE
	cp ../output-dump/cs-structure.txt ../output-dump/structure/${FILENAME}-structure.txt

	echo -e "\t\t *** PHASE-V: Making Start-End-Sensitive Code as Elastic***"
	rm -f ../output-dump/cs-elastic.i
	cp ../output-dump/cs-startendsensitive.i $PREFILE
	java -ea -Xms2048M -Xmx4096M -cp ${IMOPHOME}/third-party-tools/com.microsoft.z3.jar:.\
		-Djava.library.path=${Z3HOME}/build elasticBarrier.WorkOnImop --noPrepass -dln -ePat -f $PREFILE 2>> $ERRFILE
	cp ../output-dump/cs-elastic.i ../output-dump/elastic/${FILENAME}-elastic.i

	echo -e "======================"
	#echo -e "DUMP: $(cat $ERRFILE)"
done
