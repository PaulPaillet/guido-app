from os.path import curdir, join as pjoin
from charset_normalizer import detect
from scipy.io import wavfile

wav_fname = pjoin(curdir, 'recWav/sollong2.wav')

samplerate, data = wavfile.read(wav_fname)
print(f"number of channels = {data.shape[1]}")

length = data.shape[0] / samplerate
print(f"length = {length}s")
print(data.shape[0])

#######
# Plot

import matplotlib.pyplot as plt
import numpy as np

time = np.linspace(0., length, data.shape[0])
plt.plot(time, data[:, 0], label="Left channel")
plt.plot(time, data[:, 1], label="Right channel")
plt.legend()
plt.xlabel("Time [s]")
plt.ylabel("Amplitude")
plt.show()

##########
# Deleting blanc noise

c = 0
# 1000-1050 : [0 1]
l = []
seuil = 0
son = 0
tab = []
# for long notes, reduce the limit of 500

#100 = la base tmtc
detectNote = False

print(len(data))
for d in data:
    if abs(d[0]) > 3000:
        detectNote = True
        l.append(list(d))
    elif seuil < 20000 and detectNote:
        l.append(list(d))
        seuil +=1
    else:
        if detectNote:
            tab.append(l)
            l = []
        detectNote = False
        seuil = 0
print(len(tab))
#print(c)
#print(len(data))
#print(type(data))
#print(data)
#print(cpt)
#data_bis = np.array(np.array(l))
#print(len(data_bis))
#print(data_bis)
#print(type(l[-1]))
#print(l[-1])


for t in tab:
    data_bis = np.array(np.array(t))
    wavfile.write(str(son) + ".wav", samplerate, data_bis.astype(np.int16))
    wav_fname = pjoin(curdir, str(son) +'.wav')
    samplerate, data = wavfile.read(wav_fname)
    #print(f"number of channels = {data.shape[1]}")
    print(data)

    length = data.shape[0] / samplerate
    #print(f"length = {length}s")
    #print(data.shape[0])
    time = np.linspace(0., length, data.shape[0])

    plt.plot(time, data[:, 0], label="Left channel")
    plt.plot(time, data[:, 1], label="Right channel")
    plt.legend()
    plt.xlabel("Time [s]")
    plt.ylabel("Amplitude")
    plt.show()
    son +=1



##########
# Building new audio file
# without blanc noise