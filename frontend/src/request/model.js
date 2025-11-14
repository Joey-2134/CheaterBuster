import {API_BASE_URL} from "../util/constants.js";

export const predictPlayer = async (steamId) => {
    const response = await fetch(`${API_BASE_URL}/model/predict?steamId=${encodeURIComponent(steamId)}`, {
        method: 'POST',
    });
    if (!response.ok) throw new Error(`API error: ${response.status}`);
    return await response.json();
};
