#Speaks the word, at a chosen rate with the current word and a pause if required.
#Clears the previous word in the file so that it can be overwritten with a new word and speed parameters
#inputs are, in this order, Current word to speak, Speech Rate and if there should be a pause before the word
#speaks. 

cp /dev/null src/bashScripts/ttsSpeed.scm
if [ "$3" -gt 0 ]; then
  sleep 0.8s
fi
echo "(voice_akl_mi_pk06_cg)" >> src/bashScripts/ttsSpeed.scm
echo "(Parameter.set 'Duration_Stretch $2)" >> src/bashScripts/ttsSpeed.scm
echo "(SayText \"$1\")" >> src/bashScripts/ttsSpeed.scm
festival -b src/bashScripts/ttsSpeed.scm
