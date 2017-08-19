using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using UnityEngine.SceneManagement;

public class CounterScript : MonoBehaviour {

    public int startGold = 2;
    private int gold;
    public int nexusHP = 100;

    public int waveAmount;
    private int waveCurrentCount = 0;


	// Use this for initialization
	void Start () {
		SetCoinCounterText ();
        Cash(startGold);
        SetNexusHPText();
	}
	
	// Update is called once per frame
	void FixedUpdate () {
        SetWaveText();
	}

	public void changeTimeScale(float scale)
    {
		Time.timeScale = scale;
    }

    public bool Cash(int x)
    {
        if (gold + x < 0) { return false; }
        gold += x;
        if (x > 0)
            ApplicationStatistics.goldEarned += x;
		SetCoinCounterText ();
        return true; 
    }

    public void DamageToNexus(int x)
    {
        nexusHP -= x;
        if (nexusHP <= 0) {
            nexusHP = 0;
            GameOver(false);          
        }
        SetNexusHPText();
    }

    public void NextWave()
    {
        if (waveCurrentCount < waveAmount)
        {
            waveCurrentCount += 1;
            SetWaveText();
        }
        else GameOver(true);//?
    }

    public void GameOver(bool is_Victory)
    {
        ApplicationStatistics.is_victory = is_Victory;
        Destroy(GameObject.Find("SPAWNER"));
        GameObject.Find("fadeInOutImage").GetComponent<FadeInOut>().sceneEnd = true;
    }

	void SetCoinCounterText() {
        GameObject.Find("COINS_TEXT").GetComponent<Text>().text = gold.ToString ();
	}

    void SetNexusHPText()
    {
        GameObject.Find("HP_TEXT").GetComponent<Text>().text = nexusHP.ToString();
    }

    void SetWaveText()
    {
        GameObject.Find("WAVE_TEXT").GetComponent<Text>().text = waveCurrentCount.ToString();
        GameObject.Find("WAVE_AMOUNT_TEXT").GetComponent<Text>().text = waveAmount.ToString();
    }
}
